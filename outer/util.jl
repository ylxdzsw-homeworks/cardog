macro destructure(x)
    vars = isa(x, Symbol) ? (x, ) : x.args

    quote
        local $(map(esc, vars)...)
        try
            $([:( $(esc(i)) = req[:body][$(string(i))] ) for i in vars]...)
        catch
            return 400
        end
    end
end

macro constructure(x)
    vars  = isa(x, Symbol) ? (x, ) : x.args
    pairs = [:( $(Meta.quot(x)) => $x ) for x in vars]

    :( Dict($(pairs...)) )
end
