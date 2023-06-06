#docker build -t pricing:1.0 .
FROM eclipse-temurin:17-jdk-jammy as build

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2,rw ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-jammy

COPY --from=build target/pricing-0.0.1-SNAPSHOT.jar pricing.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms512M", "-jar", "pricing.jar"]
