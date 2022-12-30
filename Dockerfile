FROM openjdk:17-jdk-slim
WORKDIR /server

COPY target/desafio-back-votos-0.0.1-SNAPSHOT.jar /server/backend-server.jar
ENTRYPOINT ["java","-jar","backend-server.jar"]