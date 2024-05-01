FROM openjdk:21-jdk

ENV TZ=Asia/Seoul

RUN apt-get update && \
    apt-get upgrade -y

ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]