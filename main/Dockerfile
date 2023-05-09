# openjdk-11-jdk 가져오기
FROM openjdk:11-jdk

# 5000번 포트 노출
ENV PORT 8080
EXPOSE $PORT

# 빌드후 생성된 jar 파일을 컨테이너 내부에 복사
COPY main-0.0.1-SNAPSHOT.jar app.jar
COPY firebase.json usr/firebase.json


# jar 파일 실행(= BE 서버 실행)
ENTRYPOINT ["java", "-jar", "/app.jar"]