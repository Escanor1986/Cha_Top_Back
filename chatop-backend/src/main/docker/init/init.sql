-- Créer la base si elle n'existe pas
CREATE DATABASE IF NOT EXISTS chatop;

-- Créer l'utilisateur
CREATE USER IF NOT EXISTS 'chatopuser'@'%' IDENTIFIED BY 'chatoppass';
GRANT ALL PRIVILEGES ON chatop.* TO 'chatopuser'@'%';
FLUSH PRIVILEGES;
