# from autogpt import main

import asyncio
from flask import Flask
import autogpt.cli
import click
import chatgpt
import config
import papago
from pymongo import MongoClient
import datetime

app = Flask(__name__)

# db 연동
# conn = MongoClient('127.0.0.1', 27017)
conn = MongoClient(config.mongourl)

# db 생성
db = conn.chat


# FLASK에서 기본적으로 구현 하고자 하는 기능들이 존재하는 함수
@app.route("/", methods=["POST"])
def index():
    # autogpt.cli.main()
    ctx = click.Context(autogpt.cli.main, info_name='hello')
    ee = ctx.invoke(autogpt.cli.main, name='you are genuis')
    ee = ee

    # 한국어 to 영어 번역
    papago.koTOeng("안녕 난 말숙이야")

    # 영어 to 한국어 번역
    papago.engTOko("hi i am yoonhee")

    # gpt명령 내리기
    chatgpt.chattogpt("Teach me how to decide what I want to do")

    return []


@app.route("/hi", methods=["POST"])
def index1():
    # autogpt.cli.main()
    # ctx = click.Context(autogpt.cli.main, info_name='hello')
    # ee = ctx.invoke(autogpt.cli.main, name='you are genuis')
    # ee = ee

    # 여기서 collection은 mysql에서 table과 같다는 것을 의미를 한다.

    # 해당 db에 유저 를 등록을 해서 접근 가능하도록 해야 한다

    ta = "test123456"

    collection = db[ta]

    document1 = {"name": "홍길동",
                 "data": "안녕나는 시작 하고자 해23",
                 "date": datetime.datetime.utcnow()}  # 도큐먼트 생성 시간(현재 시간)입니다.

    collection.insert_one(document1)

    return []


@app.route("/hi22", methods=["POST"])
def indext1():
    asyncio(aasynctest())
    print("chatgpt작업시작")
    chatgpt.chattogpt("Teach me how to decide what I want to do")
    print("chatgpt작업 종료됨")
    return []


async def aasynctest():
    print("insert 작업 종료")
    ta = "test123456"

    collection = db[ta]

    document1 = {"name": "홍길동",
                 "data": "안녕나는 시작 하고자 해456",
                 "date": datetime.datetime.utcnow()}  # 도큐먼트 생성 시간(현재 시간)입니다.

    collection.insert_one(document1)


@app.route("/hi23", methods=["POST"])
def indexaa():
    collection = db.test2
    a = collection.find()
    for l in a:
        print(l)

    return []


app.debug = True
app.run(host="0.0.0.0", port=4000, use_reloader=False)
