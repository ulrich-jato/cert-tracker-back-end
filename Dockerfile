## Use the official OpenJDK 17 Alpine image as the base image
#FROM openjdk:17-jdk-alpine
#
## Argument for the JAR file (passed during build)
#ARG JAR_FILE=target/*.jar
#
## Copy the JAR file into the container as 'app.jar'
#COPY ${JAR_FILE} app.jar
#
## Define the entry point for running the application
#ENTRYPOINT ["java", "-jar", "/app.jar"]
#
## Labels providing metadata for the image
#LABEL maintainer="Jae <jae@example.com>, Jato <jato@example.com>, Kyle <kyle@example.com>"
#LABEL version="1.0"
#LABEL description="Docker image for the Cert Tracker Spring Boot application"



#
# Build stage
#
#FROM maven:3.8.3-openjdk-17 AS build
FROM maven:latest
# Set the build argument as an environment variable
ARG SPRING_APP_VERSION
ENV SPRING_APP_VERSION=${SPRING_APP_VERSION}
COPY .  .
#COPY src /home/app/src
#COPY pom.xml /home/app
#RUN mvn -f /home/app/pom.xml clean package
#RUN mvn -f ./pom.xml clean package
# Ensure execute permissions for the Maven Wrapper script
RUN mvn clean package
EXPOSE 8081
#ENTRYPOINT ["java","-jar","/home/app/target/cert-tracker-0.0.1-SNAPSHOT.jar"]
ENTRYPOINT ["java","-jar","./target/cert-tracker-0.0.1-SNAPSHOT.jar"]

# Build Stage
#FROM maven:3.8.3-openjdk-17 AS build
#WORKDIR /home/app/build
#COPY src pom.xml /home/app/build/
#RUN mvn -f /home/app/build/pom.xml clean install
#
#FROM maven:3.8.3-openjdk-17 AS build
#COPY src /home/app/src
#COPY pom.xml /home/app
#RUN mvn -f /home/app/pom.xml clean package
#
## Debugging: Print the contents of the target directory
#RUN ls -la /home/app/target
#
## Configuration Stage
##ARG EXPOSE_PORT=8080
#ARG JAR_FILE=/home/app/target/cert-tracker-0.0.1-SNAPSHOT.jar
#
## Final Stage
#FROM openjdk:17-jdk-alpine
#WORKDIR /app
#
## Copy the built JAR file
#COPY --from=build ${JAR_FILE} app.jar
#
## Expose the specified port
#EXPOSE 8080
#
## Set the entry point with configurable options
#ENTRYPOINT ["java", "-jar", "app.jar"]
