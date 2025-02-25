-- Créer la base si elle n'existe pas
CREATE DATABASE IF NOT EXISTS chatop;
USE chatop;

-- Créer l'utilisateur et lui accorder les droits
CREATE USER IF NOT EXISTS 'chatopuser'@'%' IDENTIFIED BY 'chatoppass';
GRANT ALL PRIVILEGES ON chatop.* TO 'chatopuser'@'%';
FLUSH PRIVILEGES;

-- Supprimer les tables existantes si elles existent déjà (évite les conflits)
DROP TABLE IF EXISTS MESSAGES;
DROP TABLE IF EXISTS RENTALS;
DROP TABLE IF EXISTS USERS;

-- Création de la table USERS
CREATE TABLE USERS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Création de la table RENTALS
CREATE TABLE RENTALS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  surface DECIMAL(10,2) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  picture VARCHAR(255),
  description TEXT,
  owner_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_rentals_owner FOREIGN KEY (owner_id) REFERENCES USERS(id) ON DELETE CASCADE
);

-- Création de la table MESSAGES
CREATE TABLE MESSAGES (
  id INT AUTO_INCREMENT PRIMARY KEY,
  rental_id INT NOT NULL,
  user_id INT NOT NULL,
  message TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_messages_rental FOREIGN KEY (rental_id) REFERENCES RENTALS(id) ON DELETE CASCADE,
  CONSTRAINT fk_messages_user FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE
);

-- Index pour optimiser les requêtes
CREATE UNIQUE INDEX USERS_email_idx ON USERS(email);

-- Message de confirmation
SELECT "✅ Base de données initialisée avec succès !" AS status;
