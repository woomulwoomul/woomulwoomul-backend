FROM openjdk:21-jdk-slim

ENV TZ=Asia/Seoul

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -yq tzdata && \
    ln -fs /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    dpkg-reconfigure -f noninteractive tzdata

ARG JAR_FILE=admin-api/build/libs/admin-app.jar
COPY ${JAR_FILE} admin-app.jar
ENTRYPOINT ["java", "-jar", "/admin-app.jar"]