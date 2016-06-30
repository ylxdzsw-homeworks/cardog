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
        "account": "57337",
        "inputObject": "all"
    })

def query_history(start_date, end_date):
    pass

def read_all_pages(res):
    yield xml.HTML(res.text)
    pass

def parse_page(page):
    def parse_row(row):
        values = map(lambda x: x.text.strip(), row.xpath("td"))
        time, _, _, _, loc, *_, amount, balance, _, status = values
        return {"time": time, "loc": loc, "amount": amount,
                "balance": balance, "status": status}

    rows = page.xpath("//table[@class='dangrichaxun']/tr")
    return list(map(parse_row, rows[1:-1]))

def main():
    username = "1133730117"
    password = "290010"

    agent = init_agent()
    login(username, password, agent)
    userid = find_userid(agent)
    cursor = query_today(userid, agent)

    result = []
    for page in read_all_pages(cursor):
        result.extend(parse_page(page))

    return result

