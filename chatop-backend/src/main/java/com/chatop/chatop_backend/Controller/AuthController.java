package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.AuthResponse;
import com.chatop.chatop_backend.dto.LoginRequest;
import com.chatop.chatop_backend.dto.RegisterRequest;
import com.chatop.chatop_backend.dto.UserDto;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour gérer les requêtes d'authentification et d'enregistrement.
 * Ce contrôleur expose des API pour enregistrer un nouvel utilisateur et authentifier un utilisateur existant.
 * Les données sont envoyées au service AuthService pour le traitement.
 * Les réponses sont renvoyées au client sous forme de ResponseEntity.
 * @RestController et @RequestMapping permettent de définir le chemin de base pour toutes les requêtes.
 * @RequiredArgsConstructor génère un constructeur avec tous les champs en lecture seule.
 * @Tag permet de regrouper les API dans la documentation Swagger.
 * @CrossOrigin permet de définir les origines autorisées pour les requêtes (à ajuster selon vos besoins de sécurité).
 * Les méthodes de ce contrôleur sont protégées par Spring Security.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs pour l'authentification et l'enregistrement")
@CrossOrigin(origins = "*") // À ajuster selon vos besoins de sécurité
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint pour enregistrer un nouvel utilisateur.
     *
     * @param request Données d'enregistrement
     * @return Réponse contenant le token JWT
     */
    @PostMapping("/register")
    @Operation(summary = "Enregistre un nouvel utilisateur")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Endpoint pour authentifier un utilisateur existant.
     *
     * @param request Données de connexion
     * @return Réponse contenant le token JWT
     */
    @PostMapping("/login")
    @Operation(summary = "Authentifie un utilisateur existant")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint pour récupérer les informations de l'utilisateur authentifié.
     * Les informations sont extraites de l'objet Authentication fourni par Spring Security.
     * Si l'utilisateur n'est pas authentifié, une réponse 401 est renvoyée.
     * @Operation permet de décrire l'opération dans la documentation Swagger.
     * @param authentication Objet d'authentification fourni par Spring Security
     * @return Réponse contenant les informations de l'utilisateur
     */
    @GetMapping("/me")
@Operation(summary = "Récupère les informations de l'utilisateur authentifié")
public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    User user = (User) authentication.getPrincipal();
    UserDto dto = new UserDto(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    return ResponseEntity.ok(dto);
}

}
