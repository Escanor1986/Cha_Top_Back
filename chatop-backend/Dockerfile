
#  FROM makes the image available to use
#  AS sets the name of the build stage
FROM maven:3.8.5-openjdk-17 AS build
# Set the working directory
WORKDIR /app
# Copy the source code
COPY pom.xml .
COPY src ./src
# RUN executes a command
RUN mvn clean package -DskipTests
RUN mvn javadoc:javadoc

# FROM makes the image available to use
#
# The openjdk:17-jdk-slim image is a lightweight image that contains the OpenJDK 17 runtime environment.
# The image is based on the Debian operating system.
FROM openjdk:17-jdk-slim
# Set the working directory
WORKDIR /app
# COPY copies files from the source to the destination
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/site/apidocs /app/apidocs
# --from=build specifies the build stage to copy files from

# EXPOSE exposes a port
EXPOSE 3001
# ENTRYPOINT specifies the command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
