# 🏡 Chatop Backend - API de location saisonnière  

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.2.0-blue?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-Build-red?logo=apachemaven)
![Last Commit](https://img.shields.io/github/last-commit/Escanor1986/Cha_Top_Back)  

🚀 **API REST pour la plateforme de mise en relation entre locataires et propriétaires** sur la côte basque.  

## ⚙️ **Tech Stack**

- **Backend** : Spring Boot 3.4.3 (Java 17)  
- **Base de données** : MySQL 8.0  
- **Sécurité** : Spring Security + JWT  
- **Conteneurisation** : Docker & Docker Compose  
- **Documentation API** : Swagger  

---

## 👤 **Structure du projet**

```sh
📺 chatop-backend/
├─ Dockerfile                     # Dockerfile pour builder l'application
├─ docker-compose.yaml            # Fichier Compose (à la racine) pour lancer les services (db, app, vault, vault-init)
├─ pom.xml                        # Fichier Maven définissant les dépendances et la configuration
├─ vault-init.sh                  # Script d'initialisation de Vault (placé à la racine)
└─ src/
    └─ main/
        ├─ java/
        │   └─ com/
        │       └─ chatop/
        │           ├─ config/
        │           │   └─ SecurityConfig.java     # Configuration de Spring Security et beans associés
        │           ├─ controller/
        │           │   ├─ AuthController.java       # Contrôleur pour l'authentification (register, login, me)
        │           │   ├─ RentalController.java       # (À implémenter pour les rentals)
        │           │   └─ MessageController.java      # (À implémenter pour les messages)
        │           ├─ dto/
        │           │   ├─ AuthResponse.java           # DTO pour la réponse d'authentification
        │           │   ├─ LoginRequest.java           # DTO pour la requête de connexion
        │           │   ├─ RegisterRequest.java        # DTO pour la requête d'enregistrement
        │           │   ├─ UserDto.java                # DTO pour l'utilisateur (utilisé par /me et UserController)
        │           │   ├─ RentalDto.java              # DTO pour les rentals (à implémenter)
        │           │   └─ MessageDto.java             # DTO pour les messages (à implémenter)
        │           ├─ exception/
        │           │   └─ AuthException.java          # Exception personnalisée pour l'authentification
        │           ├─ model/
        │           │   ├─ User.java                   # Entité utilisateur
        │           │   ├─ Rental.java                 # Entité rental (à implémenter)
        │           │   └─ Message.java                # Entité message (à implémenter)
        │           ├─ repository/
        │           │   ├─ UserRepository.java         # Accès aux données utilisateur
        │           │   ├─ RentalRepository.java       # Accès aux données des rentals
        │           │   └─ MessageRepository.java      # Accès aux données des messages
        │           ├─ security/
        │           │   ├─ JwtAuthenticationFilter.java  # Filtre pour vérifier les JWT sur chaque requête
        │           │   ├─ JwtService.java                 # Service pour générer et valider les tokens JWT
        │           │   └─ CustomUserDetailsService.java   # Service pour charger les détails d'un utilisateur
        │           └─ service/
        │               ├─ AuthService.java             # Service gérant l'enregistrement et la connexion
        │               ├─ RentalService.java           # Interface pour la logique métier des rentals (à implémenter)
        │               ├─ RentalServiceImpl.java       # Implémentation de RentalService (à implémenter)
        │               ├─ MessageService.java          # Interface pour la logique métier des messages (à implémenter)
        │               └─ MessageServiceImpl.java      # Implémentation de MessageService (à implémenter)
        └─ resources/
            ├─ application.properties    # Configuration Spring Boot (port, JPA, Swagger, etc.)
            └─ bootstrap.yaml            # Configuration de Spring Cloud Vault (et potentiellement Config Server)

```

---

## 🛠️ **Installation & Configuration**

### 📌 **1⃣ Prérequis**

Assurez-vous d’avoir :  
✅ **[Docker](https://www.docker.com/get-started/)** installé et fonctionnel  
✅ **[Java 17](https://adoptopenjdk.net/)** installé  
✅ **[Maven](https://maven.apache.org/download.cgi)** installé  
✅ (Optionnel) **[VS Code](https://code.visualstudio.com/)** avec l’extension **Database Client**

---

### 📌 **2⃣ Lancer l'application et la base de données avec Docker**

```sh
docker compose up --build -d  # Build & démarre l'application et MySQL dans Docker
```

#### ⚠️ **Problème fréquent lors du build avec Docker Desktop**

```sh
docker compose up --build -d
```

```sh
 => ERROR [app internal] load metadata for docker.io/library/maven:3.8.4-openjdk-17                                                                                                                       0.4s
 => ERROR [app internal] load metadata for docker.io/library/openjdk:17-jdk-slim                                                                                                                          0.4s
------
 > [app internal] load metadata for docker.io/library/maven:3.8.4-openjdk-17:
------
------
 > [app internal] load metadata for docker.io/library/openjdk:17-jdk-slim:
------
failed to solve: maven:3.8.4-openjdk-17: failed to resolve source metadata for docker.io/library/maven:3.8.4-openjdk-17: error getting credentials - err: exec: "docker-credential-desktop": executable file not found in $PATH, out: ``
```

#### 🧞‍♂️ **Problématique et solution**

Cela signifie que Docker ne parvient pas à trouver l'exécutable "docker-credential-desktop" pour obtenir les informations d'identification pour les images Maven/OpenJDK.

Il faut pour cela modifier le fichier config.json de Docker.

```sh
nano ~/.docker/config.json
```

✂️ **remplacer :**

```json
"credsStore": "desktop"
````

⚒️ **par :**

```json
"credsStore": ""
```

En désactivant l'utilisation d'un "credential store" qui pointe vers "desktop".

🔁 **Rebuild l'application :**

```sh
docker compose up --build -d
```

📌 **Vérifier que tout fonctionne**  

```sh
docker ps  # Voir les conteneurs actifs
docker logs -f chatop-app  # Suivre les logs de l'application Spring Boot
docker logs -f chatop-mysql  # Suivre les logs de MySQL
```

📌 **Tester l'API de base (`HealthCheck`)**  

```sh
curl -X GET http://localhost:3001/api/health
```

🔹 Devrait retourner `{ "status": "OK" }`  

📌 **Vérifier que Swagger est en place**  
👉 **[Swagger UI](http://localhost:3001/swagger-ui.html)**  
> **Remarque :** Dans certaines configurations, l'URL peut être `http://localhost:3001/swagger-ui/index.html`.

---

## 🚀 **Commandes utiles**

### 📌 **Gestion des conteneurs Docker**

```sh
docker compose down -v         # Supprime les conteneurs et les volumes
docker compose up -d --build     # Rebuild & démarre les services
docker logs -f chatop-mysql      # Suivre les logs de MySQL
```

### 📌 **Base de données**

```sh
docker exec -it chatop-mysql mysql -u chatopuser -p 
```

```sql
SHOW DATABASES;  # Vérifier les bases disponibles
USE chatop;      # Sélectionner la base de données
SHOW TABLES;     # Voir les tables existantes
```

### 📌 **Gestion du backend**

```sh
mvn clean install   # Build complet de l'application
mvn test            # Exécuter les tests
```

---

## 📝 **API Routes (Première version)**

| Méthode | Endpoint                  | Description                                              |
|---------|---------------------------|----------------------------------------------------------|
| GET     | `/api/health`             | Vérifie que l'API répond                                |
| POST    | `/api/auth/register`      | Enregistre un nouvel utilisateur (auth/register)         |
| POST    | `/api/auth/login`         | Authentifie un utilisateur (auth/login)                  |
| GET     | `/api/auth/me`            | Récupère les infos de l'utilisateur authentifié          |
| GET     | `/swagger-ui.html`        | Accès à la documentation Swagger                         |

> **Note :**  
> Les endpoints pour **rentals** et **messages** ne sont pas encore implémentés côté logique métier. Seules les entités/model existent pour ces fonctionnalités. Ces endpoints seront ajoutés dans les prochaines versions.

---

## 🔥 **Prochaines étapes**

- ✅ Finaliser l'implémentation de l'authentification (register, login, me) avec JWT  
- 🔜 **Implémenter la logique métier pour les locations (rentals)**  
- 🔜 **Implémenter la logique métier pour les messages**  
- 🔜 Sécuriser et tester l'ensemble des endpoints avec Spring Security  

---

👨‍💻 **Contribuer** :  
Clone et fork le projet, puis soumets tes pull requests pour améliorer et ajouter de nouvelles fonctionnalités ! 🚀

💡 **Besoin d'aide ?**  
Ouvre une issue ou contacte-moi sur **GitHub**.

---
