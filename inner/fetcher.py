# encoding: utf-8

import requests as req
from lxml import etree as xml

def init_agent():
    cookie = {**req.get("http://172.31.7.16/homeLogin.action").cookies}
    header = {
        "Accept"          : "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
        "Accept-Encoding" : "gzip, deflate",
        "Accept-Language" : "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4,zh-TW;q=0.2",
        "User-Agent"      : "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36"
    }
    return {"cookies": cookie, "headers": header}

def login(username, password, agent):
    req.get("http://172.31.7.16/getCheckpic.action?rand=9021.368313552755", **agent)
    req.post("http://172.31.7.16/loginstudent.action", **agent, data={
        "name"            : username,
        "userType"        : "1",
        "passwd"          : password,
        "loginType"       : "2",
        "rand"            : "9021",
        "imageField.x"    : "24",
        "imageField.y"    : "13"
    })

def find_userid(agent):
    res = req.get('http://172.31.7.16/accountcardUser.action', **agent)
    res = xml.HTML(res.text)
    return res.xpath("/html/body/table/tr/td/table/tr[2]/th/table/tr/th/table/tr[2]/td[4]/div")[0].text

def query_today(userid, agent):
    return req.post('http://172.31.7.16/accounttodatTrjnObject.action', **agent, data={
        "account"     : userid,
        "inputObject" : "all"
    })

def query_history(userid, start_date, end_date, agent):
    guide_page = req.get('http://172.31.7.16/accounthisTrjn.action', **agent)
    query_link = retrive_link_from_page(guide_page)
    query_page = req.post("http://172.31.7.16" + query_link, **agent, data={
        "account"     : userid,
        "inputObject" : "all"
    })
    wait_link = retrive_link_from_page(query_page)
    wait_page = req.post("http://172.31.7.16" + wait_link, **agent, data={
        "inputStartDate" : start_date,
        "inputEndDate"   :   end_date
    })
    result_link = retrive_link_from_page(wait_page)
    return req.post("http://172.31.7.16/accounthisTrjn.action" + result_link, **agent)

def retrive_link_from_page(page):
    return xml.HTML(page.text).xpath("//form")[0].attrib["action"]

def read_all_pages(res, agent):
    while True:
        current_page = xml.HTML(res.text)
        yield current_page
        try:
            if "下一页" in res.text:
                a, b, pageid = map(lambda x: x.attrib["value"], current_page.xpath("//input[@type='hidden']"))
                res = req.post("http://172.31.7.16/accountconsubBrows.action", **agent, data={
                    "inputStartDate" : a,
                    "inputEndDate"   : b,
                    "pageNum"        : str(int(pageid) + 1)
                })
            else:
                break
        except Exception as e:
            print(e)
            break

def parse_page(page):
    rows = page.xpath("//table[@class='dangrichaxun']/tr")
    return list(map(parse_row, rows[1:-1]))

def parse_row(row):
    values = map(lambda x: x.text.strip(), row.xpath("td"))
    time, _, _, _, loc, *_, amount, balance, _, status = values
    return {"time": time, "loc": loc, "amount": amount,
            "balance": balance, "status": status}

def fetch(query, username, password):
    agent = init_agent()
    login(username, password, agent)
    userid = find_userid(agent)
    cursor = query(userid, agent)
    result = []
    for page in read_all_pages(cursor, agent):
        result.extend(parse_page(page))
    return result

def fetch_today(*args):
    return fetch(query_today, *args)

def fetch_history(start_date, end_date, *args):
    query = lambda userid, agent: query_history(userid, start_date, end_date, agent)
    return fetch(query, *args)
