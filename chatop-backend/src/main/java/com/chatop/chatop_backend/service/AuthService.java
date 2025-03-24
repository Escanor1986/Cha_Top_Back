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

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
     * @throws EmailAlreadyInUseException si l'email est déjà utilisé
     * @throws UserNotFoundException si l'utilisateur n'est pas trouvé après l'enregistrement
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Début de l'enregistrement d'un nouvel utilisateur avec l'email: {}", request.getEmail());
        
        try {
            // Vérifie si l'email existe déjà
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                logger.warn("Tentative d'enregistrement avec un email déjà utilisé: {}", request.getEmail());
                throw new EmailAlreadyInUseException("Email already in use");
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
            logger.debug("Utilisateur créé en base de données: {}", user.getId());
            
            // Récupère l'utilisateur depuis le repository pour s'assurer qu'il a bien été enregistré
            user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("Utilisateur introuvable juste après l'enregistrement: {}", request.getEmail());
                    return new UserNotFoundException("User not found after registration");
                });
            
            // Génère un token JWT
            var jwtToken = jwtService.generateToken(user);
            logger.info("Enregistrement réussi et JWT généré pour l'utilisateur: {}", user.getId());
            
            // Retourne la réponse
            return new AuthResponse(jwtToken, user.getId(), user.getEmail(), user.getName());
        } catch (EmailAlreadyInUseException | UserNotFoundException e) {
            // Propager les exceptions spécifiques pour qu'elles soient gérées par le contrôleur
            throw e;
        } catch (Exception e) {
            // Logguer les erreurs inattendues et les propager
            logger.error("Erreur inattendue lors de l'enregistrement: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Authentifie un utilisateur existant.
     *
     * @param request Données de connexion
     * @return Réponse d'authentification avec le token JWT
     * @throws AuthenticationFailedException si l'authentification échoue
     * @throws UserNotFoundException si l'utilisateur n'est pas trouvé
     */
    public AuthResponse login(LoginRequest request) {
        logger.info("Tentative de connexion pour l'email: {}", request.getEmail());
        
        try {
            // Authentifie l'utilisateur avec Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            // Récupère l'utilisateur depuis la base de données
            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        logger.error("Utilisateur non trouvé lors de la connexion: {}", request.getEmail());
                        return new UserNotFoundException("User not found");
                    });
            
            // Génère un token JWT
            var jwtToken = jwtService.generateToken(user);
            logger.info("Connexion réussie pour l'utilisateur: {}", user.getId());
            
            // Retourne la réponse
            return new AuthResponse(jwtToken, user.getId(), user.getEmail(), user.getName());
        } catch (BadCredentialsException e) {
            logger.warn("Échec d'authentification pour l'email: {}", request.getEmail());
            throw new AuthenticationCredentialsNotFoundException("Invalid credentials", e);
        } catch (UserNotFoundException e) {
            // Propager l'exception pour qu'elle soit gérée par le contrôleur
            throw e;
        } catch (Exception e) {
            // Logguer les erreurs inattendues et les propager
            logger.error("Erreur inattendue lors de la connexion: {}", e.getMessage(), e);
            throw e;
        }
    }
}
