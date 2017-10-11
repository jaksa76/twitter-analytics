FROM openjdk:9-jre-slim

COPY ./target/master.jar /usr/src/ta/master/
WORKDIR /usr/src/ta/master

ENV ENV=production

EXPOSE 80

CMD ["java", "-jar", "master.jar"]
