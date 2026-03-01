FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
ARG MODULE
COPY pom.xml .
COPY common/pom.xml common/
COPY ${MODULE}/pom.xml ${MODULE}/
COPY common/src common/src
COPY ${MODULE}/src ${MODULE}/src
RUN apk add --no-cache maven && \
    mvn -pl common,${MODULE} -am package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ARG MODULE
COPY --from=build /app/${MODULE}/target/*.jar app.jar
EXPOSE 8080 8081 8082 8083 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
