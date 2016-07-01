using HttpServer
using Restful
using Restful: json, cors, staticserver, JLT
using OhMyJulia

include("util.jl")

type DataLock
    serial_num::Int
    new_data::Condition

    DataLock() = new(0, Condition())
end

const commands = []
const datalock = DataLock()

@resource root let
    :mixin => defaultmixin
    :onreturn => cors
end

@resource command <: root let
    :mixin => defaultmixin

    :GET | json => begin
        if !isempty(commands)
            shift!(commands)
        else
            Dict(:type => "null")
        end
    end
end

@resource records <: root let
    :mixin => defaultmixin

    :POST | json => begin
        notify(datalock.new_data, req[:body])
        200
    end
end

@resource query <: root let
    :mixin => defaultmixin

    :POST | json => begin
        @destructure username, password, period

        query_id = datalock.serial_num += 1

        command = Dict(:username => username, :password => password, :serial_num => query_id)

        if period == "today"
            command[:type]       = "today"
        elseif period == "lastmonth"
            command[:type]       = "history"
            command[:end_date]   = Dates.format(now(), "yyyymmdd")
            command[:start_date] = Dates.format(now() - Dates.Month(1), "yyyymmdd")
        else
            return 400
        end

        push!(commands, command)

        while true
            res = wait(datalock.new_data)
            if res["serial_num"] == query_id
                return res["data"]
            else
                continue
            end
        end
    end
end

@async run(Server(root), host=ip"0.0.0.0", port=10086)

isinteractive() || wait()
