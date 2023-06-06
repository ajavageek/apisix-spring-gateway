#docker build -t stocks:1.0 .
FROM eclipse-temurin:17-jdk-jammy as build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2,rw ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-jammy

COPY --from=build target/stocks-0.0.1-SNAPSHOT.jar stocks.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms512M", "-jar", "stocks.jar"]
