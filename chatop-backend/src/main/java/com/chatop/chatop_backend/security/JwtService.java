package com.chatop.chatop_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service pour la gestion des tokens JWT.
 * Cette classe fournit des méthodes pour générer, valider et extraire des informations des tokens JWT.
 * Elle utilise la bibliothèque jjwt pour manipuler les tokens JWT.
 * @Service indique que cette classe est un bean Spring.
 * @see org.springframework.security.core.userdetails.UserDetails
 */
@Service
public class JwtService {

    // Clé secrète pour signer les tokens JWT (à définir dans application.properties)
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    // Durée de validité du token (en millisecondes) - 24 heures par défaut
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Génère un token JWT pour un utilisateur.
     *
     * @param userDetails Détails de l'utilisateur
     * @return Token JWT généré
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Génère un token JWT avec des claims additionnels.
     *
     * @param extraClaims Claims additionnels à inclure dans le token
     * @param userDetails Détails de l'utilisateur
     * @return Token JWT généré
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Vérifie si un token JWT est valide pour un utilisateur donné en utilisant les détails de l'utilisateur et la clé secrète. la clé secret est définie dans le fichier application.properties.
     *
     * @param token Token JWT à vérifier
     * @param userDetails Détails de l'utilisateur
     * @return true si le token est valide, false sinon
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Vérifie si un token JWT a expiré.
     *
     * @param token Token JWT à vérifier
     * @return true si le token a expiré, false sinon
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration d'un token JWT.
     *
     * @param token Token JWT
     * @return Date d'expiration
     */
    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait le nom d'utilisateur (subject) d'un token JWT.
     *
     * @param token Token JWT
     * @return Nom d'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une claim spécifique d'un token JWT.
     *
     * @param token Token JWT
     * @param claimsResolver Fonction pour résoudre la claim
     * @param <T> Type de la claim
     * @return Valeur de la claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les claims d'un token JWT.
     *
     * @param token Token JWT
     * @return Toutes les claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Récupère la clé de signature pour les tokens JWT.
     *
     * @return Clé de signature
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
