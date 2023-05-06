import os
import sys
import urllib.request
import json
import config

# 현재 상태는 영어를 한국어로 번역하는 부분을 의미를 함
client_id = config.client_id  # 개발자센터에서 발급받은 Client ID 값
client_secret = config.client_secret  # 개발자센터에서 발급받은 Client Secret 값


def koTOeng(text):
    # 현재 상태는 영어를 한국어로 번역하는 부분을 의미를 함

    encText = urllib.parse.quote(text)
    data = "source=ko&target=en&text=" + encText

    url = "https://openapi.naver.com/v1/papago/n2mt"
    request = urllib.request.Request(url)
    request.add_header("X-Naver-Client-Id", client_id)
    request.add_header("X-Naver-Client-Secret", client_secret)
    response = urllib.request.urlopen(request, data=data.encode("utf-8"))
    rescode = response.getcode()

    if (rescode == 200):
        response_body = response.read()
        tt=response_body.decode("utf-8")
        temp_data_dict = json.loads(tt)
        a1=temp_data_dict["message"]
        a2=a1["result"]
        a3=a2["translatedText"]
        return a3
    else:
        print("Error Code:" + rescode)


def engTOko(text):
    encText = urllib.parse.quote(text)
    data = "source=en&target=ko&text=" + encText

    url = "https://openapi.naver.com/v1/papago/n2mt"
    request = urllib.request.Request(url)
    request.add_header("X-Naver-Client-Id", client_id)
    request.add_header("X-Naver-Client-Secret", client_secret)
    response = urllib.request.urlopen(request, data=data.encode("utf-8"))
    rescode = response.getcode()

    if (rescode == 200):
        response_body = response.read()
        print(response_body.decode('utf-8'))
    else:
        print("Error Code:" + rescode)


