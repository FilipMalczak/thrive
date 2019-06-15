FROM openjdk:8-jre-alpine

LABEL maintainer="Filip Malczak (filip.malczak@gmail.com)"
COPY ./build/libs/*-boot.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
