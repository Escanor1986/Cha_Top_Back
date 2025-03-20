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
- **Base de données** : MySQL 8.2.0  
- **Sécurité** : Spring Security + JWT  
- **Secrets & Configuration** : Spring Cloud Vault avec HashiCorp Vault
- **Conteneurisation** : Docker & Docker Compose  
- **Documentation API** : Swagger/OpenAPI 3  

---

## 👤 **Structure du projet**

```sh
📺chatop
   └── config
   │    └── OpenApiConfig
   │ 
   └── Chatop-backend/
        ├── Dockerfile                      # Dockerfile pour builder l'application
        ├── docker-compose.yaml             # Fichier Compose pour lancer les services (DB, app, Vault, Vault-init)
        ├── pom.xml                         # Fichier Maven définissant les dépendances et la configuration
        ├── vault-init.sh                   # Script d'initialisation de Vault
        ├── .gitignore                      # Ignore les fichiers non suivis par Git
        ├── .gitattributes                  # Définit les attributs spécifiques à Git
        ├── mvnw.cmd                        # Script pour exécuter Maven Wrapper sous Windows
        └── src/
            └── main/
                ├── java/
                │   └── com/
                │       └── chatop/
                │           ├── config/
                │           │   ├── SecurityConfig.java         # Configuration de Spring Security et beans associés
                │           │   ├── WebConfig.java              # Configuration de Spring pour servir les fichiers statiques (uploads)
                │           ├── controller/
                │           │   ├── AuthController.java         # Contrôleur pour l'authentification (register, login, me)
                │           │   ├── RentalController.java       # Contrôleur pour la gestion des locations (CRUD)
                │           │   ├── MessageController.java      # Contrôleur pour la gestion des messages
                │           ├── dto/
                │           │   ├── AuthResponse.java           # DTO pour la réponse d'authentification
                │           │   ├── LoginRequest.java           # DTO pour la requête de connexion
                │           │   ├── RegisterRequest.java        # DTO pour la requête d'enregistrement
                │           │   ├── UserDto.java                # DTO pour les utilisateurs (/me, etc.)
                │           │   ├── RentalDto.java              # DTO pour les locations
                │           │   ├── MessageDto.java             # DTO pour les messages (avec JsonProperty pour mapping)
                │           │   ├── ResponseMessage.java        # DTO standard pour les messages de réponse d'API
                │           ├── exception/
                │           │   ├── GlobalExceptionHandler.java # Gestion centralisée des exceptions
                │           │   ├── AuthException.java          # Exception personnalisée pour l'authentification
                │           │   ├── UserNotFoundException.java  # Exception pour utilisateur non trouvé
                │           │   ├── EmailAlreadyInUseException.java  # Exception si l'email est déjà utilisé
                │           ├── model/
                │           │   ├── User.java                   # Entité utilisateur
                │           │   ├── Rental.java                 # Entité location (rental)
                │           │   ├── Message.java                # Entité message (avec annotations @Builder)
                │           ├── repository/
                │           │   ├── UserRepository.java         # Accès aux données utilisateur
                │           │   ├── RentalRepository.java       # Accès aux données des locations
                │           │   ├── MessageRepository.java      # Accès aux données des messages
                │           ├── security/
                │           │   ├── JwtAuthenticationFilter.java  # Filtre pour vérifier les JWT sur chaque requête (logs améliorés)
                │           │   ├── JwtService.java               # Service pour générer et valider les tokens JWT (logs améliorés)
                │           │   ├── CustomUserDetailsService.java # Service pour charger les détails d'un utilisateur (logs améliorés)
                │           ├── service/
                │           │   ├── AuthService.java             # Service gérant l'authentification
                │           │   ├── RentalService.java           # Interface pour la logique métier des locations
                │           │   ├── RentalServiceImpl.java       # Implémentation de la gestion des locations
                │           │   ├── MessageService.java          # Interface pour la logique métier des messages
                │           │   ├── MessageServiceImpl.java      # Implémentation de MessageService (complètement fonctionnelle)
                │           │   ├── FileStorageService.java      # Service de gestion des fichiers (logs améliorés, noms de fichiers uniques)
                │           ├── utils/
                │           │   ├── VaultPropertiesLogger.java   # Logger des propriétés Vault pour la gestion des secrets
                └── resources/
                    ├── application.properties  # Configuration Spring Boot mise à jour (limite taille upload, logs)
                    ├── bootstrap.yaml          # Configuration Spring Cloud Vault
                    ├── static/
                    │   └── uploads/            # Dossier pour stocker les images uploadées
                    └── logs/                   # Dossier pour stocker les fichiers de logs

```

---

## 📋 **Guide rapide pour l'évaluation**

Pour démarrer ce projet rapidement, suivez ces étapes pour une installation et un test efficaces:

## 📌 **1⃣ Prérequis**

Assurez-vous d'avoir :  
✅ **[Docker](https://www.docker.com/get-started/)** installé et fonctionnel  
✅ **[Java 17](https://adoptopenjdk.net/)** installé  
✅ **[Maven](https://maven.apache.org/download.cgi)** installé  
✅ (Optionnel) **[VS Code](https://code.visualstudio.com/)** avec l'extension **Database Client**

### 🚀 **1. Cloner le repository**

```sh
git clone https://github.com/Escanor1986/Cha_Top_Back.git
cd Cha_Top_Back/chatop-backend
```

### 🔧 **2. Créer un fichier .env**

Créez un fichier `.env` à la racine du projet avec les variables suivantes:

```env
MYSQL_DATABASE=chatop
MYSQL_USER=chatopuser
MYSQL_PASSWORD=chatoppass
MYSQL_ROOT_PASSWORD=rootpassword
VAULT_TOKEN=00000000-0000-0000-0000-000000000000
```

### 🐳 **3. Lancer l'application avec Docker Compose**

```sh
docker compose up --build -d
```

> ⚠️ **Première exécution**: Attendez environ 1-2 minutes que tous les services soient correctement initialisés.

### ✅ **4. Vérifier que tout fonctionne**

Vérifiez que les conteneurs sont bien démarrés:

```sh
docker ps
```

Vous devriez voir 4 conteneurs en cours d'exécution:
- `chatop-app`
- `chatop-mysql`
- `chatop-vault`
- `chatop-vault-init` (peut être en état "Exited" car il s'exécute une seule fois)

### 🔍 **5. Explorer la documentation API avec Swagger**

Accédez à l'interface Swagger pour tester l'API:

```
http://localhost:3001/swagger-ui.html
```

### 🧪 **6. Tests rapides de l'API**

#### 📝 Créer un utilisateur

```http
POST http://localhost:3001/api/auth/register
Content-Type: application/json

{
  "email": "test@example.com",
  "name": "Test User",
  "password": "password123"
}
```

#### 🔑 Se connecter

```http
POST http://localhost:3001/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "password123"
}
```

La réponse contiendra un token JWT que vous pouvez utiliser pour les requêtes authentifiées.

#### 👤 Vérifier l'authentification

```http
GET http://localhost:3001/api/auth/me
Authorization: Bearer <votre_token_jwt>
```

---

## 🛠️ **Installation & Configuration complètes**

### 📌 **1⃣ Prérequis**

Assurez-vous d'avoir :  
✅ **[Docker](https://www.docker.com/get-started/)** installé et fonctionnel  
✅ **[Java 17](https://adoptopenjdk.net/)** installé  
✅ **[Maven](https://maven.apache.org/download.cgi)** installé  
✅ (Optionnel) **[VS Code](https://code.visualstudio.com/)** avec l'extension **Database Client**

---

### 📌 **2⃣ Lancer l'application avec Docker Compose**

Le fichier docker-compose.yaml intègre désormais les services suivants :

- **db** : Base de données MySQL
- **vault** : Service Vault (HashiCorp) avec healthcheck utilisant `vault status`
- **vault-init** : Service d'initialisation pour charger les secrets dans Vault
- **app** : Application Spring Boot configurée pour utiliser Spring Cloud Vault

Pour lancer l'ensemble des services, vous pouvez exécuter la commande suivante :

```sh
docker compose up --build -d  # Build & démarre l'application et MySQL dans Docker
```

`Note` : Assurez-vous que les variables d'environnement soient correctement définies dans votre environnement ou dans un fichier .env.

```env
MYSQL_DATABASE=
MYSQL_USER=
MYSQL_PASSWORD=
MYSQL_ROOT_PASSWORD=
VAULT_TOKEN=
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

---

### 🔒 **Intégration de Spring Cloud Vault**

- **bootstrap.yaml :**
Contient la configuration Spring Cloud Vault pour lire les secrets stockés dans Vault (ex. : configuration JWT, accès à la base de données, etc.).

- **vault-init.sh :**
Script qui initialise Vault, active le moteur de secrets (KV) et charge les secrets et policies requis par l'application.

- **docker-compose.yml :**
Les services vault et vault-init ont été ajoutés/modifiés pour supporter l'intégration.
Le service vault déclare notamment :

- ```VAULT_DEV_ROOT_TOKEN_ID``` pour définir le token racine
- ```VAULT_DEV_LISTEN_ADDRESS``` pour le binding
- Un healthcheck basé sur la commande vault status
Le service vault-init dépend de Vault et exécute le script d'initialisation lors de son démarrage.

`Conseil` : En environnement de développement, Vault fonctionne en mode "dev" (en mémoire et non sécurisé pour la production).

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

### 📌 **Dépannage rapide**

Si l'application ne démarre pas correctement:

1. **Vérifier les logs de l'application**:
   ```sh
   docker logs chatop-app
   ```

2. **Redémarrer les services**:
   ```sh
   docker compose restart
   ```

3. **Reconstruire complètement**:
   ```sh
   docker compose down -v
   docker compose up --build -d
   ```

4. **Vérifier la connexion à la base de données**:
   ```sh
   docker exec -it chatop-mysql mysql -u chatopuser -p
   # Entrez le mot de passe défini dans le fichier .env
   ```

---

## 🚀 **Endpoints API disponibles**

| Méthode | Endpoint               | Description                                      | Authentification    |
|---------|------------------------|--------------------------------------------------|---------------------|
| GET     | `/api/health`          | Vérifie que l'API répond                         | Non                 |
| POST    | `/api/auth/register`   | Enregistre un nouvel utilisateur                 | Non                 |
| POST    | `/api/auth/login`      | Authentifie un utilisateur                       | Non                 |
| GET     | `/api/auth/me`         | Récupère les infos de l'utilisateur authentifié  | JWT Bearer Token    |
| GET     | `/api/rentals`         | Liste toutes les locations disponibles           | JWT Bearer Token    |
| GET     | `/api/rentals/{id}`    | Détails d'une location spécifique                | JWT Bearer Token    |
| POST    | `/api/rentals`         | Créer une nouvelle location (multipart/form-data)| JWT Bearer Token    |
| PUT     | `/api/rentals/{id}`    | Met à jour une location existante                | JWT Bearer Token    |
| POST    | `/api/messages`        | Envoyer un message                               | JWT Bearer Token    |
| GET     | `/swagger-ui.html`     | Accès à la documentation Swagger                 | Non                 |

---

## 📋 **Exemple de requête complète pour créer une location**

Avec cURL:

```sh
curl -X POST "http://localhost:3001/api/rentals" \
  -H "Authorization: Bearer <votre_token_jwt>" \
  -F "name=Appartement vue mer" \
  -F "surface=85" \
  -F "price=1200" \
  -F "description=Magnifique appartement avec vue sur l'océan" \
  -F "picture=@/chemin/vers/image.jpg"
```

Avec Postman:
1. Choisir la méthode `POST`
2. URL: `http://localhost:3001/api/rentals`
3. Headers: `Authorization: Bearer <votre_token_jwt>`
4. Body: Form-data
   - name: Appartement vue mer
   - surface: 85
   - price: 1200
   - description: Magnifique appartement avec vue sur l'océan
   - picture: [Sélectionner un fichier]

---

## 🔥 **Prochaines étapes**

- ✅ Finaliser l'implémentation de l'authentification (register, login, me) avec JWT  
- ✅ **Implémenter la logique métier pour les locations (rentals)**  
- ✅ **Implémenter la logique métier pour les messages**  
- ✅ Sécuriser et tester l'ensemble des endpoints avec Spring Security  
- 🔜 Envisager la gestion de Vault en production (configuration sécurisée, stockage persistant, etc.)  

---

👨‍💻 **Contribuer** :  
Clone et fork le projet, puis soumets tes pull requests pour améliorer et ajouter de nouvelles fonctionnalités ! 🚀

💡 **Besoin d'aide ?**  
Ouvre une issue ou contacte-moi sur **GitHub**.

---
