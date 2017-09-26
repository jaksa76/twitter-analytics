FROM openjdk:9-jre-slim

COPY ./target/master.jar /usr/src/ta/master
WORKDIR /usr/src/ta/master

EXPOSE 4567

CMD ["java", "-jar", "master.jar"]
