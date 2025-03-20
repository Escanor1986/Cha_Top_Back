FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
# Copie des fichiers sources
COPY pom.xml .
COPY src ./src
# Compilation du projet et génération du JAR
RUN mvn clean package -DskipTests
# Génération de la documentation JavaDoc
RUN mvn javadoc:javadoc

FROM openjdk:17-jdk-slim
WORKDIR /app
# Installer Nginx pour servir la documentation JavaDoc
RUN apt-get update && apt-get install -y nginx
# Copier le JAR de l'application
COPY --from=build /app/target/*.jar app.jar
# Copier les fichiers JavaDoc générés
COPY --from=build /app/target/site/apidocs /javadoc
# Exposer les ports
EXPOSE 3001 80
# Démarrer Nginx et l'application Spring Boot
CMD ["sh", "-c", "nginx && java -jar app.jar"]
