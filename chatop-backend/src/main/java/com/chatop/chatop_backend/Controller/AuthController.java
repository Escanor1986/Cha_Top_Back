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
@CrossOrigin(origins = "*") // À ajuster selon vos besoins
public class AuthController {

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
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'enregistrement")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
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
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'authentification")
    })
    @CrossOrigin(origins = "*") // permet à tous les domaines d'accéder à cette route
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
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
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Récupération de l'utilisateur à partir du principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Transformation de User en UserDto
        UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
        return ResponseEntity.ok(dto);
    }
}
