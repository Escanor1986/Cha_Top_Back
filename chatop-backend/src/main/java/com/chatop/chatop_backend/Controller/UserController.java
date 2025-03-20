package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.UserDto;
import com.chatop.chatop_backend.exception.UserNotFoundException;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User", description = "APIs pour gérer les utilisateurs")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);
  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // JavaDoc
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
      @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
  })
  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
    log.info("👤 Récupération de l'utilisateur avec l'ID: {}", id);

    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.error("⚠️ Utilisateur non trouvé avec l'ID: {}", id);
          return new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        });

    log.info("✅ Utilisateur trouvé: {}", user.getEmail());

    // !Conversion en DTO pour ne pas exposer les données sensibles
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setName(user.getName());
    userDto.setEmail(user.getEmail());
    userDto.setCreatedAt(user.getCreatedAt()); // Passer directement le LocalDateTime
    userDto.setUpdatedAt(user.getUpdatedAt()); // Passer directement le LocalDateTime

    return ResponseEntity.ok(userDto);
  }
}
