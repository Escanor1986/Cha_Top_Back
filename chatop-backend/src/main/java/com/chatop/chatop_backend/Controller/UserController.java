package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.UserDto;
import com.chatop.chatop_backend.exception.UserNotFoundException;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur pour gérer les utilisateurs.
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "APIs pour gérer les utilisateurs")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
    log.info("🔌 UserController initialisé avec succès");
  }

  /**
   * Récupère un utilisateur par son ID.
   * 
   * @param id ID de l'utilisateur
   * @return Utilisateur correspondant à l'ID
   * @throws UserNotFoundException Si l'utilisateur n'est pas trouvé
   */
  @Operation(summary = "Récupère un utilisateur par son ID", description = "Renvoie les informations d'un utilisateur à partir de son identifiant")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
      @ApiResponse(responseCode = "400", description = "ID utilisateur invalide"),
      @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
      @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
  })
  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable Long id) {
    String requestId = UUID.randomUUID().toString();
    log.info("📥 [{}] Réception d'une requête de récupération de l'utilisateur avec l'ID: {}", requestId, id);

    try {
      // Validation de l'ID
      if (id == null || id <= 0) {
        log.warn("⚠️ [{}] ID utilisateur invalide: {}", requestId, id);
        return ResponseEntity.badRequest()
            .body(Collections.singletonMap("message", "L'ID utilisateur doit être un nombre positif"));
      }

      // Recherche de l'utilisateur
      log.debug("🔍 [{}] Recherche de l'utilisateur avec l'ID: {}", requestId, id);
      Optional<User> userOpt = userRepository.findById(id);
      
      if (userOpt.isEmpty()) {
        log.warn("⚠️ [{}] Utilisateur non trouvé avec l'ID: {}", requestId, id);
        throw new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
      }
      
      User user = userOpt.get();
      log.debug("✅ [{}] Utilisateur trouvé: {}", requestId, user.getEmail());
      
      // Conversion en DTO pour ne pas exposer les données sensibles
      log.trace("🔄 [{}] Conversion de l'utilisateur en DTO", requestId);
      UserDto userDto = new UserDto();
      userDto.setId(user.getId());
      userDto.setName(user.getName());
      userDto.setEmail(user.getEmail());
      userDto.setCreatedAt(user.getCreatedAt());
      userDto.setUpdatedAt(user.getUpdatedAt());
      
      log.info("✅ [{}] Utilisateur récupéré avec succès: ID={}, Email={}", requestId, user.getId(), user.getEmail());
      return ResponseEntity.ok(userDto);
      
    } catch (UserNotFoundException e) {
      // Exception spécifique pour utilisateur non trouvé
      log.warn("⚠️ [{}] {}", requestId, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Collections.singletonMap("message", e.getMessage()));
          
    } catch (Exception e) {
      // Erreur inattendue
      log.error("❌ [{}] Erreur inattendue lors de la récupération de l'utilisateur: {}", requestId, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("message", "Erreur lors de la récupération de l'utilisateur"));
          
    } finally {
      log.info("🏁 [{}] Fin du traitement de la requête de récupération de l'utilisateur", requestId);
    }
  }
  
  /**
   * Récupère les informations du profil de l'utilisateur actuellement authentifié.
   * À implémenter selon les besoins de l'application.
   */
  @Operation(summary = "Récupère le profil de l'utilisateur actuel", description = "Renvoie les informations de l'utilisateur actuellement authentifié")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profil utilisateur récupéré avec succès"),
      @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
      @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
  })
  @GetMapping("/me")
  public ResponseEntity<?> getCurrentUserProfile() {
    String requestId = UUID.randomUUID().toString();
    log.info("📥 [{}] Réception d'une requête de récupération du profil utilisateur courant", requestId);
    
    // Note: Cette méthode est un placeholder. Il faudrait l'implémenter en utilisant 
    // l'authentification Spring Security pour récupérer l'utilisateur actuel.
    log.warn("⚠️ [{}] Méthode getCurrentUserProfile non implémentée", requestId);
    
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
        .body(Collections.singletonMap("message", "Cette fonctionnalité n'est pas encore implémentée"));
  }
}
