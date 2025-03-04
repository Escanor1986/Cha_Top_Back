package com.chatop.chatop_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Service pour la gestion des tokens JWT.
 * <p>
 * Cette classe fournit des méthodes pour générer, extraire et valider des tokens JWT.
 * Elle utilise la bibliothèque JJWT pour la manipulation des tokens.
 * </p>
 */
@Service
public class JwtService {

    // Clé secrète utilisée pour signer les tokens JWT.
    // Note : Dans un environnement de production, utilisez une clé sécurisée et configurée via des variables d'environnement
    private static final String SECRET_KEY = "votre_clé_secrète_ici_vraiment_longue_et_complexe";

    /**
     * Génère la clé de signature à partir de la clé secrète.
     *
     * @return la clé de signature {@link SecretKey}
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Génère un token JWT pour le sujet spécifié.
     *
     * @param subject le sujet pour lequel le token est généré (généralement l'identifiant de l'utilisateur)
     * @return le token JWT généré
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 1000 * 60 * 60 * 24); // 24 heures de validité

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Valide le token JWT en vérifiant son intégrité et sa validité.
     *
     * @param token   le token JWT à valider
     * @param subject le sujet attendu (généralement l'identifiant de l'utilisateur)
     * @return {@code true} si le token est valide et correspond au sujet attendu, {@code false} sinon
     */
    public boolean validateToken(String token, String subject) {
        try {
            final String tokenSubject = extractClaim(token, Claims::getSubject);
            return (subject.equals(tokenSubject) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait une revendication (claim) spécifique du token JWT.
     *
     * @param <T>            le type de la revendication
     * @param token          le token JWT
     * @param claimsResolver une fonction pour extraire la revendication des {@link Claims}
     * @return la valeur de la revendication extraite
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les revendications (claims) du token JWT.
     *
     * @param token le token JWT
     * @return les revendications {@link Claims} extraites du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Vérifie si le token JWT est expiré.
     *
     * @param token le token JWT
     * @return {@code true} si le token est expiré, {@code false} sinon
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}


/**
 ** Exemple d'utilisation de la classe JwtService dans un contrôleur Spring Boot
 Je vais vous expliquer en détail le rôle de cette classe `JwtService` dans le processus d'authentification et de login.

## Rôle Général du Service JWT

Le `JwtService` est un composant crucial dans la gestion de l'authentification basée sur les JSON Web Tokens (JWT). Il gère trois fonctions principales :

1. **Génération de Tokens**
2. **Validation des Tokens**
3. **Extraction d'Informations des Tokens**

## Processus Détaillé

### 1. Génération du Token (`generateToken`)

Lorsqu'un utilisateur se connecte avec succès, cette méthode crée un token unique qui :
- Contient l'identifiant de l'utilisateur
- A une durée de validité de 24 heures
- Est signé cryptographiquement pour garantir son intégrité

### 2. Validation du Token (`validateToken`)

Lors de chaque requête authentifiée, cette méthode vérifie que le token :
- Correspond à l'utilisateur attendu
- N'est pas expiré
- A été signé avec la clé secrète

### 3. Extraction des Informations (`extractClaim`)

Permet de récupérer des informations spécifiques du token, comme :
- L'identifiant de l'utilisateur
- La date d'émission
- La date d'expiration

## Sécurité et Fonctionnement

- La clé secrète (`SECRET_KEY`) sert à signer et vérifier l'authenticité des tokens
- Chaque token est unique et temporaire
- Les tokens expirent après 24 heures, obligeant à une reconnexion

## Exemple de Flux Typique

1. Connexion de l'utilisateur
2. Vérification des credentials
3. Génération d'un token JWT
4. Renvoi du token au client
5. Le client utilise ce token pour les requêtes suivantes
6. Le serveur valide le token à chaque requête

Ce service est un élément essentiel de la sécurité de l'application Spring Boot, gérant l'authentification de manière sécurisée et stateless.
  */
