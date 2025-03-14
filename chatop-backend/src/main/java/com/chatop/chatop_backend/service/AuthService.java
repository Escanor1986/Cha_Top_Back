package com.chatop.chatop_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chatop.chatop_backend.dto.AuthResponse;
import com.chatop.chatop_backend.dto.LoginRequest;
import com.chatop.chatop_backend.dto.RegisterRequest;
import com.chatop.chatop_backend.exception.EmailAlreadyInUseException;
import com.chatop.chatop_backend.exception.UserNotFoundException;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.UserRepository;
import com.chatop.chatop_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service gérant l'authentification et l'enregistrement des utilisateurs.
 * Ce service contient les méthodes pour enregistrer un nouvel utilisateur et authentifier un utilisateur existant.
 * Les données sont envoyées au repository UserRepository pour accéder aux données des utilisateurs.
 * Les mots de passe sont hachés avec un encodeur de mot de passe.
 * Les tokens JWT sont générés avec le service JwtService.
 * @Service indique que cette classe est un service Spring.
 * @RequiredArgsConstructor génère un constructeur avec tous les champs en lecture seule.
 * @see com.chatop.chatop_backend.repository.UserRepository
 * @see com.chatop.chatop_backend.security.JwtService
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Enregistre un nouvel utilisateur dans le système.
     *
     * @param request Données d'enregistrement
     * @return Réponse d'authentification avec le token JWT
     */
    public AuthResponse register(RegisterRequest request) {
        // Vérifie si l'email existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        // Crée un nouvel utilisateur
        var user = User.builder()
                .name(request.getName())// Récupère le nom
                .email(request.getEmail()) // Récupère l'email
                .password(passwordEncoder.encode(request.getPassword())) // Hache le mot de passe
                .createdAt(LocalDateTime.now()) // Récupère la date de création
                .updatedAt(LocalDateTime.now()) // Récupère la date de mise à jour
                .build(); // Construit l'utilisateur
        
        // Sauvegarde l'utilisateur
        userRepository.save(user);

        //! Debogage
       logger.info("User registered: {}", user);
        
        // Log l'utilisateur depuis le repository
        user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        //! Debogage
        logger.info("User registered from repository: {}", user);

        // Génère un token JWT
        var jwtToken = jwtService.generateToken(user);
        
        // Retourne la réponse
        return new AuthResponse(jwtToken, user.getId(), user.getEmail(), user.getName());
    }

    /**
     * Authentifie un utilisateur existant.
     *
     * @param request Données de connexion
     * @return Réponse d'authentification avec le token JWT
     */
    public AuthResponse login(LoginRequest request) {
        // Authentifie l'utilisateur avec Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // Récupère l'utilisateur depuis la base de données
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Génère un token JWT
        var jwtToken = jwtService.generateToken(user);
        
        // Retourne la réponse
        return new AuthResponse(jwtToken, user.getId(), user.getEmail(), user.getName());
    }
}
