FROM openjdk:21-slim

ENV TZ=Asia/Seoul

RUN apt update && \
    apt upgrade -y

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]