FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
RUN mvn javadoc:javadoc

FROM openjdk:17-jdk-slim
WORKDIR /app
# Installer Nginx
RUN apt-get update && apt-get install -y nginx
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/site/apidocs /javadoc
# Copier la configuration Nginx
RUN echo 'server { \
    listen 80; \
    server_name localhost; \
    location /javadoc/ { \
        alias /app/apidocs/; \
        index index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf
# Exposer les ports
EXPOSE 3001 80
# Démarrer Nginx avec votre application
CMD ["sh", "-c", "nginx && java -jar app.jar"]
