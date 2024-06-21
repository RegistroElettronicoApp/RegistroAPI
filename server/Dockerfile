FROM openjdk:21
LABEL authors="chri"
WORKDIR /app

COPY server/build/libs/server-all.jar /app/reg-server.jar

EXPOSE 4473
EXPOSE 4474

ENTRYPOINT ["java", "-jar", "/app/reg-server.jar"]