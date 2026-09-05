FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/aurea-0.0.1-SNAPSHOT.jar aurea.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "aurea.jar"]