# 🏡 Chatop Backend - API de location saisonnière  

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-brightgreen?logo=springboot)  
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)  
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)  
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)  
![Maven](https://img.shields.io/badge/Maven-Build-red?logo=apachemaven)  

🚀 **API REST pour la plateforme de mise en relation entre locataires et propriétaires** sur la côte basque.  

## ⚙️ **Tech Stack**

- **Backend** : Spring Boot 3.4.3 (Java 17)  
- **Base de données** : MySQL 8.0  
- **Sécurité** : Spring Security + JWT  
- **Conteneurisation** : Docker & Docker Compose  
- **Documentation API** : Swagger  

---

## 👤 **Structure du projet**

``` 
📺 chatop-backend
 └─ src
    ├─ main
    │   └─ java
    │       └─ com.chatop
    │           ├─ controller   # Contrôleurs REST
    │           ├─ service      # Services (business logic)
    │           ├─ repository   # Gestion des accès DB (JPA)
    │           └─ models       # Entités JPA
    ├─ resources
    │   └─ application.properties  # Configuration Spring Boot
    ├─ src/main/docker
    │   ├─ docker-compose.yaml  # Configuration Docker Compose
    │   └─ init
    │       └─ init.sql  # Script d'initialisation MySQL
    ├─ pom.xml       # Dépendances Maven
    ├─ mvnw          # Wrapper Maven
    ├─ README.md     # La doc du projet
```

---

## 🛠️ **Installation & Configuration**

### 📌 **1️⃣ Prérequis**

Assurez-vous d’avoir :  
✅ **[Docker](https://www.docker.com/get-started/)** installé et fonctionnel  
✅ **[Java 17](https://adoptopenjdk.net/)** installé  
✅ **[Maven](https://maven.apache.org/download.cgi)** installé  
✅ **[VS Code](https://code.visualstudio.com/)** avec l’extension **Database Client** (optionnel)  

---

### 📌 **2️⃣ Lancer le projet avec Docker**

```sh
docker compose up -d  # Démarre MySQL dans un conteneur
```

📌 **Vérifier que MySQL tourne bien**  

```sh
docker ps | grep mysql  # Vérifier que le conteneur MySQL est actif
docker logs chatop-mysql  # Voir les logs du conteneur
```

📌 **Se connecter à MySQL via Docker**  

```sh
docker exec -it chatop-mysql mysql -u chatopuser -p
```

(Mot de passe : `chatoppass`)

---

### 📌 **3️⃣ Lancer l'application Spring Boot**

```sh
./mvnw spring-boot:run  # Si Maven Wrapper est utilisé
```

ou

```sh
mvn spring-boot:run  # Si Maven est installé
```

📌 **Tester l'API de base (`HealthCheck`)**  

```sh
curl -X GET http://localhost:8080/api/health
```

🔹 Devrait retourner : `{ "status": "OK" }`  

📌 **Vérifier que Swagger est en place**  
👉 **[Swagger UI](http://localhost:8080/swagger-ui.html)**

---

## 🚀 **Commandes utiles**

### 📌 **Gestion des conteneurs Docker**

```sh
docker compose down -v  # Supprime les conteneurs et les volumes
docker compose up -d --build  # Rebuild & démarre les services
docker logs -f chatop-mysql  # Suivre les logs de MySQL
```

### 📌 **Base de données**

```sh
SHOW DATABASES;  # Vérifier les bases disponibles
USE chatop;  # Sélectionner la base de données
SHOW TABLES;  # Voir les tables existantes
```

### 📌 **Gestion du backend**

```sh
mvn clean install  # Build complet de l'application
mvn test  # Exécuter les tests
```

---

## 📝 **API Routes (Première version)**

| Méthode | Endpoint            | Description                |
|---------|---------------------|----------------------------|
| GET     | `/api/health`       | Vérifier si l'API répond  |
| GET     | `/swagger-ui.html`  | Accès à la doc API        |

📌 **D'autres endpoints seront ajoutés au fur et à mesure !**  

---

## 🔥 **Prochaines étapes**

✅ Ajouter la gestion des utilisateurs (Auth JWT)  
✅ Implémenter les endpoints CRUD des annonces de location  
✅ Sécuriser les routes avec **Spring Security**  

👨‍💻 **Contribuer** : Clone et fork le projet, et let's go ! 🚀

---

💡 **Besoin d'aide ?** Ouvre une issue ou ping-moi sur **GitHub**. 🚀🔥
