FROM openjdk:9-jre-slim

COPY ./target/twitter-analytics-0.0.1-SNAPSHOT.jar /usr/src/web-app/
COPY ./service-account.json /usr/src/web-app/
WORKDIR /usr/src/web-app

EXPOSE 80

CMD ["java", "-jar", "twitter-analytics-0.0.1-SNAPSHOT.jar"]
