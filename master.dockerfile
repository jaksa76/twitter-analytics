FROM openjdk:8u141-jre-slim
COPY ./target/master.jar /usr/src/twitter/
WORKDIR /usr/src/twitter
EXPOSE 4567
CMD ["java", "-jar", "master.jar"]