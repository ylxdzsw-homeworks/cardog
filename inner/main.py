# encoding: utf-8

import json
import time
import requests as req
from fetcher import fetch_today, fetch_history

host = "http://127.0.0.1:10086"

while True:
    command = json.loads(req.get(host+"/command").text)
    print(command)
    try:
        if command['type'] == 'null':
            time.sleep(2)
            continue
        if command['type'] == 'today':
            result = fetch_today(command['username'], command['password'])
        if command['type'] == 'history':
            result = fetch_history(command['start_date'], command['end_date'],
                                   command['username'], command['password'])
    except Exception as e:
        print(e)
        result = 502
    # print(result)
    req.post(host+"/records", json={
        "serial_num": command["serial_num"],
        "data": result
    })
