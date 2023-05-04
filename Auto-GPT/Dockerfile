# 파이썬 버전
FROM python:3.8.5

WORKDIR /AutoGPT
COPY . .
RUN pip install -r requirements.txt

EXPOSE 4000

CMD python ./main.py