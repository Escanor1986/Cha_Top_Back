package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.AuthResponse;
import com.chatop.chatop_backend.dto.LoginRequest;
import com.chatop.chatop_backend.dto.RegisterRequest;
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
            throw new RuntimeException("Email already in use");
        }

        // Crée un nouvel utilisateur
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Sauvegarde l'utilisateur
        userRepository.save(user);
        
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
