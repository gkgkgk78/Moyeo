from flask import Flask, request, current_app
import autogpt.cli
import click
import chatgpt
import config
import papago
from pymongo import MongoClient
import datetime
import sys
import io
import json

import logging

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

app = Flask(__name__)

# db 연동
# conn = MongoClient('127.0.0.1', 27017)
conn = MongoClient(config.mongourl)

# db 생성
db = conn.chat

# 로그 설정
logging.basicConfig(level=logging.INFO, format='[%(asctime)s] %(levelname)s in %(module)s: %(message)s')

# FLASK에서 기본적으로 구현 하고자 하는 기능들이 존재하는 함수
@app.route("/", methods=["POST"])
def index():
    # autogpt.cli.main()
    data_header = dict(request.headers)
    request_data = request.get_data()
    # ah1=request.get_json()
    temp_data = request_data.decode("utf-8")
    print(temp_data + "1", file=sys.stdout)
    temp_data+=" The result is less than or equal to 4000 tokens."

    # with app.app_context():
    #     # 로그 출력
    #     current_app.logger.info(f'temp_data: {temp_data}')

    # print("꺼낸 데이터:", temp_data)
    ctx = click.Context(autogpt.cli.main, info_name='hello')

    # 밑의 name에서 보내주고자 하는 내용이 담길 것임
    ee = ctx.invoke(autogpt.cli.main, name=temp_data)
    print(ee + "3" , file=sys.stdout)

    # with app.app_context():
    #     # 로그 출력
    #     current_app.logger.info(f'ee: {ee}')

    #want_data = data_header["Title"]
    want_data = data_header.get("Title", "")  # data_header 딕셔너리에서 "Title" 키에 해당하는 값을 추출. 없으면 빈 문자열을 반환함

    #   return json.dumps({"result": ee}), 200, {'Content-Type': 'application/json'}

    to_chatgpt = ee

    if want_data == "restaurant":
        to_chatgpt += "\n" + config.restaurant
    elif want_data == "activity":
        to_chatgpt += "\n" + config.activity
    else:
        to_chatgpt += "\n" + config.place

    # 한국어 to 영어 번역
    # text_to_english = papago.koTOeng("안녕 난 말숙이야")
    # print(text_to_english)
    # 영어 to 한국어 번역
    # papago.engTOko("hi i am yoonhee")
    #print(to_chatgpt)
    # gpt명령 내리기

    last = chatgpt.chattogpt(to_chatgpt)
    print(last + "4" , file=sys.stdout)

    # with app.app_context():
    #     # 로그 출력
    #     current_app.logger.info(f'last: {last}')

    return json.dumps({"result": last}), 200, {'Content-Type': 'application/json'}


@app.route("/hi", methods=["POST"])
def index1():
    ta = "test123456"
    collection = db[ta]
    document1 = {"name": "홍길동",
                 "data": "안녕나는 시작 하고자 해23",
                 "date": datetime.datetime.utcnow()}  # 도큐먼트 생성 시간(현재 시간)입니다.
    collection.insert_one(document1)
    return []


@app.route("/hi23", methods=["POST"])
def indexaa():
    collection = db.test2
    a = collection.find()
    for l in a:
        print(l)

    return []


app.debug = True
app.run(host="0.0.0.0", port=4000, use_reloader=False)
