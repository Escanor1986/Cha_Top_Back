# 🏡 Chatop Backend - API de Location Saisonnière

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.2.0-blue?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Nginx](https://img.shields.io/badge/Nginx-1.27.4-009639?logo=nginx)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Maven](https://img.shields.io/badge/Maven-Build-red?logo=apachemaven)
![Last Commit](https://img.shields.io/github/last-commit/Escanor1986/Cha_Top_Back)

## 📑 Sommaire

- [Présentation](#-présentation)
- [Technologies](#-technologies)
- [Installation rapide](#-installation-rapide)
- [Architecture](#-architecture)
- [Configuration détaillée](#-configuration-détaillée)
- [API Endpoints](#-api-endpoints)
- [Documentation](#-documentation)
- [Dépannage](#-dépannage)
- [Prochaines évolutions](#-prochaines-évolutions)
- [Contribution](#-contribution)

## 🚀 Présentation

Chatop est une plateforme de mise en relation entre locataires et propriétaires sur la côte basque. Cette API REST sécurisée permet de gérer les locations saisonnières, les utilisateurs et les messages entre participants.

Le système offre les fonctionnalités suivantes :

- Authentification sécurisée avec JWT
- Gestion des utilisateurs (inscription, connexion, profil)
- Gestion des biens immobiliers (création, liste, détails)
- Système de messagerie entre locataires et propriétaires
- Stockage sécurisé des secrets avec HashiCorp Vault

## ⚙️ Technologies

L'application repose sur un stack technique moderne et robuste :

- **Backend** : Spring Boot 3.4.3 (Java 17)
- **Base de données** : MySQL 8.2.0
- **Sécurité** : Spring Security + JWT
- **Gestion des secrets** : Spring Cloud Vault avec HashiCorp Vault
- **Conteneurisation** : Docker & Docker Compose
- **Documentation API** : Swagger/OpenAPI 3
- **Documentation technique** : JavaDoc

## 🏁 Installation rapide

### Prérequis

- [Docker](https://www.docker.com/get-started/) et Docker Compose
- [Java 17](https://adoptopenjdk.net/)
- [Maven](https://maven.apache.org/download.cgi)

### 1. Cloner le repository

```bash
git clone https://github.com/Escanor1986/Cha_Top_Back.git
cd Cha_Top_Back/chatop-backend
```

### 2. Créer un fichier .env

```env
MYSQL_DATABASE=chatop
MYSQL_USER=chatopuser
MYSQL_PASSWORD=chatoppass
MYSQL_ROOT_PASSWORD=rootpassword
VAULT_TOKEN=00000000-0000-0000-0000-000000000000
```

### 3. Lancer l'application

```bash
docker compose up --build -d
```

> ⚠️ **Note**: Lors du premier démarrage, attendez 1-2 minutes pour l'initialisation complète des services.

### 4. Vérifier le déploiement

```bash
docker ps
```

Vous devriez voir les conteneurs suivants actifs :

- `chatop-app` - Application Spring Boot
- `chatop-mysql` - Base de données MySQL
- `chatop-vault` - Service HashiCorp Vault
- `chatop-javadoc` - Serveur Nginx pour la documentation JavaDoc

## 🏗️ Architecture

```
📦 chatop-backend
 ┣ 📂 src/main
 ┃ ┣ 📂 java/com/chatop
 ┃ ┃ ┣ 📂 config        # Configuration Spring (Security, Web)
 ┃ ┃ ┣ 📂 controller    # Contrôleurs REST (Auth, Rental, Message)
 ┃ ┃ ┣ 📂 dto           # Objets de transfert de données
 ┃ ┃ ┣ 📂 exception     # Exceptions personnalisées et handler global
 ┃ ┃ ┣ 📂 model         # Entités JPA (User, Rental, Message)
 ┃ ┃ ┣ 📂 repository    # Interfaces d'accès aux données
 ┃ ┃ ┣ 📂 security      # Filtres et services liés à la sécurité JWT
 ┃ ┃ ┣ 📂 service       # Services métier
 ┃ ┃ ┗ 📂 utils         # Classes utilitaires
 ┃ ┗ 📂 resources
 ┃   ┣ 📄 application.properties  # Configuration Spring Boot
 ┃   ┣ 📄 bootstrap.yaml          # Configuration Spring Cloud Vault
 ┃   ┗ 📂 static/uploads          # Stockage des images
 ┣ 📄 Dockerfile         # Configuration de build de l'image
 ┣ 📄 docker-compose.yml # Orchestration des services Docker
 ┣ 📄 nginx.conf         # Configuration du serveur Nginx pour JavaDoc
 ┣ 📄 vault-init.sh      # Script d'initialisation de Vault
 ┗ 📄 pom.xml            # Configuration Maven
```

## ⚙️ Configuration détaillée

### Docker Compose

Le fichier `docker-compose.yml` orchestre 5 services principaux :

1. **db** : Base de données MySQL avec healthcheck
2. **vault** : Service HashiCorp Vault pour la gestion des secrets
3. **vault-init** : Service d'initialisation des secrets dans Vault
4. **app** : Application Spring Boot
5. **javadoc** : Serveur Nginx pour la documentation technique

### Intégration de Vault

L'application utilise Spring Cloud Vault pour récupérer de manière sécurisée :

- Les informations de connexion à la base de données
- Les clés de signature JWT
- Autres secrets sensibles

Le service `vault-init` configure automatiquement Vault avec les secrets nécessaires au démarrage.

### Configuration réseau

Tous les services sont connectés via un réseau dédié `chatop-network` qui isole les communications.

## 🔌 API Endpoints

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

### Exemple de création d'une location

```bash
curl -X POST "http://localhost:3001/api/rentals" \
  -H "Authorization: Bearer <votre_token_jwt>" \
  -F "name=Appartement vue mer" \
  -F "surface=85" \
  -F "price=1200" \
  -F "description=Magnifique appartement avec vue sur l'océan" \
  -F "picture=@/chemin/vers/image.jpg"
```

## 📚 Documentation

### Swagger UI

L'API est documentée avec OpenAPI 3.0 et accessible via Swagger UI à :

```
http://localhost:3001/swagger-ui.html
```

Cette interface permet de :

- Explorer tous les endpoints disponibles
- Voir les modèles de données attendus
- Tester l'API directement depuis le navigateur

### JavaDoc

La documentation technique du code est générée automatiquement lors du build et accessible à :

```
http://localhost:8888/javadoc/
```

Elle fournit des informations détaillées sur l'implémentation des classes, méthodes et interfaces.

## 🔧 Dépannage

### Problème lors du build Docker

Si vous rencontrez l'erreur :

```
failed to solve: maven:3.8.4-openjdk-17: failed to resolve source metadata
```

Solution :

1. Modifiez le fichier `~/.docker/config.json`
2. Remplacez `"credsStore": "desktop"` par `"credsStore": ""`
3. Relancez le build

### Autres problèmes courants

1. **L'application ne démarre pas**:

   ```bash
   docker logs chatop-app
   ```

2. **Redémarrer les services**:

   ```bash
   docker compose restart
   ```

3. **Reconstruire entièrement**:

   ```bash
   docker compose down -v
   docker compose up --build -d
   ```

4. **Vérifier la base de données**:

   ```bash
   docker exec -it chatop-mysql mysql -u chatopuser -p
   ```

## 🔮 Prochaines évolutions

- ✅ Implémentation complète de l'authentification JWT
- ✅ Gestion des locations avec upload d'images
- ✅ Système de messagerie entre utilisateurs
- ✅ Sécurisation des données sensibles avec Vault
- ✅ Documentation complète (JavaDoc, Swagger)
- 🔜 Configuration Vault pour environnement de production
- 🔜 Ajout de tests d'intégration complets
- 🔜 CI/CD avec GitHub Actions

## 👥 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Clonez et forkez le projet
2. Créez une branche pour votre fonctionnalité
3. Développez et testez vos modifications
4. Soumettez une pull request

Pour toute question ou suggestion, n'hésitez pas à ouvrir une issue ou à me contacter directement sur GitHub.

---

*Développé avec ❤️ pour faciliter la location saisonnière sur la côte basque*
