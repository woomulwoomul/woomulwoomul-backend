FROM openjdk:21

ENV TZ=Asia/Seoul

RUN apt update && \
    apt upgrade -y && \
    rm -rf /var/cache/apk/*

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]