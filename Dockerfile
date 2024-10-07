FROM maven:3.8.3-openjdk-17 AS build
#FROM maven:3.8.4-eclipse-temurin-17-alpine
# Set the build argument as an environment variable
ARG SPRING_APP_VERSION
ENV SPRING_APP_VERSION=${SPRING_APP_VERSION}
#COPY .  .
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package
#RUN mvn -f ./pom.xml clean package
# Ensure execute permissions for the Maven Wrapper script
#RUN mvn clean package
EXPOSE 8081
ENTRYPOINT ["java","-jar","/home/app/target/cert-tracker-0.0.1-SNAPSHOT.jar"]
