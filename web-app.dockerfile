FROM openjdk:8u141-jre-slim
COPY ./target/web-app.jar /usr/src/twitter/
WORKDIR /usr/src/twitter
EXPOSE 4568
CMD ["java", "-jar", "web-app.jar"]