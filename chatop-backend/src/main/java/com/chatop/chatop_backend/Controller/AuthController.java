package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.AuthResponse;
import com.chatop.chatop_backend.dto.LoginRequest;
import com.chatop.chatop_backend.dto.RegisterRequest;
import com.chatop.chatop_backend.dto.UserDto;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.UserRepository;
import com.chatop.chatop_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping("/register")
    @Operation(summary = "Enregistre un nouvel utilisateur")
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
    @PostMapping("/login")
    @Operation(summary = "Authentifie un utilisateur existant")
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
    @GetMapping("/me")
    @Operation(summary = "Récupère les informations de l'utilisateur authentifié")
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
