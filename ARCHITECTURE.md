# 🏗️ Architecture de l’Application ChâTop Backend

## 📌 1. Introduction

Cette application est le back-end de la plateforme **ChâTop**, un portail de mise en relation entre locataires et propriétaires pour la location saisonnière.

Elle est développée en **Java + Spring Boot** et utilise **MySQL** pour la base de données.

---

## 📂 **Structure du Projet**

L'application suit une architecture en couches, organisée comme suit :

```sh
src 
 └── main 
  └── java 
   └── com 
    └── chatop 
     └── chatop_backend 
      ├── controller 
      │ └── AuthController.java 
      ├── service 
      │ └── AuthService.java 
      ├── repository 
      │ └── UserRepository.java 
      ├── model 
      │ └── User.java 
      └── security 
        ├── JwtTokenProvider.java 
        ├── JwtAuthenticationFilter.java 
        ├── SecurityConfig.java 
        └── CustomUserDetailsService.java
```

### 📌 **Détails des packages :**

- **`controller/`** : Contient les classes gérant les requêtes HTTP entrantes, comme `AuthController` pour les opérations d'authentification.
- **`service/`** : Inclut la logique métier, par exemple `AuthService` pour gérer les opérations liées à l'authentification.
- **`repository/`** : Gère l'accès aux données, avec des interfaces comme `UserRepository` pour interagir avec la base de données des utilisateurs.
- **`model/`** : Définit les entités de votre application, telles que `User`.
- **`security/`** : Contient les classes spécifiques à la sécurité, notamment :
  - **`JwtTokenProvider.java`** : Gère la création et la validation des tokens JWT.
  - **`JwtAuthenticationFilter.java`** : Filtre les requêtes HTTP pour traiter les tokens JWT.
  - **`SecurityConfig.java`** : Configure les aspects de sécurité de l'application.
  - **`CustomUserDetailsService.java`** : Charge les détails de l'utilisateur lors de l'authentification.

---

## 📌 3. Routes de l’API

L’API expose les routes suivantes :

### **🔐 Authentification**

| Méthode | Endpoint            | Description                     | Sécurisé |
|---------|---------------------|---------------------------------|----------|
| `POST`  | `/api/auth/register` | Inscription d’un utilisateur   | ❌ Non  |
| `POST`  | `/api/auth/login`    | Connexion d’un utilisateur     | ❌ Non  |
| `GET`   | `/api/auth/me`       | Récupérer les infos utilisateur | ✅ Oui |

### **🏡 Gestion des Locations**

| Méthode | Endpoint            | Description                               | Sécurisé |
|---------|---------------------|-------------------------------------------|----------|
| `GET`   | `/api/rentals`       | Liste des locations disponibles          | ✅ Oui  |
| `GET`   | `/api/rentals/{id}`  | Détails d’une location spécifique        | ✅ Oui  |
| `POST`  | `/api/rentals`       | Ajouter une nouvelle location            | ✅ Oui  |
| `PUT`   | `/api/rentals/{id}`  | Modifier une location existante          | ✅ Oui  |

### **📩 Gestion des Messages**

| Méthode | Endpoint            | Description                                  | Sécurisé |
|---------|---------------------|----------------------------------------------|----------|
| `POST`  | `/api/messages`      | Envoyer un message à un propriétaire       | ✅ Oui  |

### **👤 Gestion des Utilisateurs**

| Méthode | Endpoint            | Description                        | Sécurisé |
|---------|---------------------|------------------------------------|----------|
| `GET`   | `/api/user/{id}`     | Récupérer les infos d’un utilisateur | ✅ Oui |

---

## **📦 4. Technologies & Dépendances**

L’application utilise les technologies suivantes :

- **Spring Boot** (framework Java)
- **Spring Security & JWT** (authentification)
- **Spring Data JPA** (gestion de la base de données)
- **MySQL** (base de données)
- **Swagger** (documentation API)
- **Postman** (tests API)
- **Mockoon** (mock du back-end)

### **📌 Dépendances essentielles (`pom.xml`)**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>
</dependencies>
```

---

## **🚀 5. Lancer l’Application**

### **📌 Prérequis**

- **Java 17**
- **Maven**
- **Docker** (pour MySQL)
- **Mockoon** (pour le mock de l’API)
- **Postman** (pour tester l’API)

### **📌 Étapes**

1. **Lancer MySQL avec Docker :**

   ```sh
   docker compose -f src/main/docker/docker-compose.yaml up -d
   ```

2. **Lancer l’API Spring Boot :**

   ```sh
   mvn spring-boot:run
   ```

3. **Tester l’API avec Postman ou Swagger :**
   👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## **📌 6. Plan d’implémentation**

### **🛠️ Étapes à suivre**

✅ **1. Créer les entités JPA** (`User`, `Rental`, `Message`)  
✅ **2. Créer les repositories** (accès base de données)  
✅ **3. Implémenter l'authentification avec JWT**  
✅ **4. Implémenter les endpoints REST**  
✅ **5. Ajouter la documentation Swagger**  
✅ **6. Tester et valider avec Postman**  

---

## **🎯 Conclusion**

Ce fichier `ARCHITECTURE.md` sert de **guide** pour tout développeur travaillant sur ce projet.  
Il permet de **comprendre rapidement** l’architecture et **facilite le développement et la maintenance**.  

---
