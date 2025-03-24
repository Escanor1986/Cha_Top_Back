package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.AuthResponse;
import com.chatop.chatop_backend.dto.LoginRequest;
import com.chatop.chatop_backend.dto.RegisterRequest;
import com.chatop.chatop_backend.dto.UserDto;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.UserRepository;
import com.chatop.chatop_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour gérer l'authentification et l'inscription des utilisateurs.
 * 
 * @RestController: Indique à Spring qu'il s'agit d'un contrôleur REST.
 * @RequestMapping: Indique le préfixe commun pour toutes les routes définies dans ce contrôleur.
 * !Dans ce cas, toutes les routes commenceront par /api/auth.
 * @RequiredArgsConstructor: Génère un constructeur avec tous les arguments de la classe marqués comme @NonNull.
 * @Tag: Indique que ce contrôleur est lié à la documentation Swagger sous le tag "Authentication".
 * @CrossOrigin: Permet à tous les domaines d'accéder à cette route.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs pour l'authentification et l'enregistrement")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthService authService;
    private final UserRepository userRepository;

    /**
     * Endpoint pour enregistrer un nouvel utilisateur.
     * On utilise @Valid pour valider les champs avant de les traiter.
     *
     * @param request Données d'enregistrement
     * @return Réponse contenant le token JWT
     */
    @Operation(summary = "Enregistre un nouvel utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur enregistré avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'enregistrement"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Tentative d'enregistrement pour l'email: {}", request.getEmail());
        
        try {
            AuthResponse response = authService.register(request);
            logger.info("Enregistrement réussi pour l'email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de l'utilisateur: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement: " + e.getMessage());
        }
    }

    /**
     * Endpoint pour authentifier un utilisateur existant.
     * @Valid permet d'éviter de recevoir des données incorrectes.
     *
     * @param request Données de connexion
     * @return Réponse contenant le token JWT
     */
    @Operation(summary = "Authentifie un utilisateur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur authentifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'authentification"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @CrossOrigin(origins = "*") // permet à tous les domaines d'accéder à cette route
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Tentative de connexion pour l'email: {}", request.getEmail());
        
        try {
            AuthResponse response = authService.login(request);
            logger.info("Connexion réussie pour l'email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion de l'utilisateur: {}", e.getMessage(), e);
            
            if (e.getMessage().contains("identifiants invalides")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Identifiants invalides");
            }
            
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la connexion: " + e.getMessage());
        }
    }

    /**
     * Endpoint pour récupérer les informations de l'utilisateur authentifié.
     * - Récupère l'utilisateur en base de données avec son email extrait de Authentication.
     *
     * @param authentication Objet d'authentification fourni par Spring Security
     * @return Réponse contenant les informations de l'utilisateur
     */
    @Operation(summary = "Récupère les informations de l'utilisateur authentifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                logger.warn("Tentative d'accès non authentifiée à /me");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
            }

            // Récupération de l'utilisateur à partir du principal
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            
            logger.info("Récupération des informations de l'utilisateur: {}", email);
            
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("Utilisateur non trouvé en base de données: {}", email);
                        return new UsernameNotFoundException("Utilisateur non trouvé");
                    });

            // Transformation de User en UserDto
            UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
            
            logger.info("Informations de l'utilisateur {} récupérées avec succès", email);
            return ResponseEntity.ok(dto);
            
        } catch (UsernameNotFoundException e) {
            logger.error("Utilisateur non trouvé: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des informations utilisateur: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne du serveur: " + e.getMessage());
        }
    }
}
