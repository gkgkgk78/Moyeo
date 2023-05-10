# 파이썬 버전
FROM python:3.8.5

# 클론해서 만들어질 디렉토리 이름입니다.
WORKDIR /AutoGPT
#  모든 파일을 WORKDIR로 복사
COPY . .
RUN pip install -r requirements.txt

EXPOSE 4000

CMD python ./main.py