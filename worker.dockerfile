FROM openjdk:8u141-jre-slim
COPY ./service-account.json /usr/src/twitter/
COPY ./target/worker.jar /usr/src/twitter/
WORKDIR /usr/src/twitter
ENV ENV=production
CMD ["java", "-jar", "worker.jar"]