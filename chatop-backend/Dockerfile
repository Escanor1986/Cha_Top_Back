# Étape 1 : Build de l'application avec Maven
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline  # Télécharge les dépendances en avance
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécuter l'application avec OpenJDK
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
