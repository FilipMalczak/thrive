FROM openjdk:8-jre-alpine

LABEL maintainer="Filip Malczak (filip.malczak@gmail.com)"

COPY ./build/libs/*-boot.jar ./app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
