FROM openjdk:9-jre-slim

COPY ./target/web-app.jar /usr/src/ta/web-app/
COPY ./service-account.json /usr/src/ta/web-app/
WORKDIR /usr/src/ta/web-app

EXPOSE 4567

CMD ["java", "-jar", "web-app.jar"]
