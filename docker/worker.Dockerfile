FROM openjdk:9-jre-slim

COPY ./target/worker.jar /usr/src/ta/worker/
COPY ./service-account.json /usr/src/ta/worker/
WORKDIR /usr/src/ta/worker

ENV ENV=production

CMD ["java", "-jar", "worker.jar"]