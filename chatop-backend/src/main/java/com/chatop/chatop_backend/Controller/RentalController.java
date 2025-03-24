package com.chatop.chatop_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chatop.chatop_backend.dto.RentalDto;
import com.chatop.chatop_backend.exception.UserNotFoundException;
import com.chatop.chatop_backend.service.FileStorageService;
import com.chatop.chatop_backend.service.RentalService;
import com.chatop.chatop_backend.repository.UserRepository;
import com.chatop.chatop_backend.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Cette classe est un contrôleur REST qui expose les différentes routes pour
 * gérer les locations.
 * Elle permet de gérer les requêtes HTTP pour les locations.
 * 
 * @RestController: Indique à Spring qu'il s'agit d'un contrôleur REST.
 * @RequestMapping: Indique le préfixe commun pour toutes les routes définies dans ce contrôleur.
 *!Dans ce cas, toutes les routes commenceront par /api/rentals.
 */
@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rental API Controller", description = "Exposes endpoints to manage rentals")
public class RentalController {

    private static final Logger log = LoggerFactory.getLogger(RentalController.class);
    private final FileStorageService fileStorageService;
    private final RentalService rentalService;
    private final UserRepository userRepository;

    public RentalController(RentalService rentalService, FileStorageService fileStorageService,
            UserRepository userRepository) {
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        log.info("🔌 RentalController initialisé avec succès");
    }

    /**
     * Récupère toutes les locations.
     * 
     * @return Liste de toutes les locations
     */
    @Operation(summary = "Récupère toutes les locations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Locations récupérées avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucune location trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<?> getAllRentals() {
        String requestId = UUID.randomUUID().toString(); // Génère un ID de requête unique
        log.info("📥 [{}] Réception d'une requête de récupération de toutes les locations", requestId);
        
        try {
            log.debug("🔍 [{}] Appel du service pour récupérer toutes les locations", requestId);
            List<RentalDto> rentals = rentalService.getAllRentals();

            if (rentals.isEmpty()) {
                log.warn("⚠️ [{}] Aucune location trouvée dans la base de données", requestId);
                return ResponseEntity.noContent().build();
            } else {
                log.info("✅ [{}] {} locations récupérées avec succès", requestId, rentals.size());
                return ResponseEntity.ok(Collections.singletonMap("rentals", rentals));
            }
        } catch (Exception e) {
            log.error("❌ [{}] Erreur lors de la récupération des locations: {}", requestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur lors de la récupération des locations"));
        }
    }

    /**
     * Récupère une location par son ID.
     * 
     * @param id ID de la location
     * @return Location correspondant à l'ID
     */
    @Operation(summary = "Récupère une location par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Location trouvée"),
        @ApiResponse(responseCode = "404", description = "Location non trouvée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getRentalById(@PathVariable Long id) {
        String requestId = UUID.randomUUID().toString();
        log.info("📥 [{}] Réception d'une requête de récupération de la location avec l'ID: {}", requestId, id);
        
        try {
            log.debug("🔍 [{}] Recherche de la location avec l'ID: {}", requestId, id);
            Optional<RentalDto> rentalOpt = rentalService.getRentalById(id);
            
            if (rentalOpt.isPresent()) {
                log.info("✅ [{}] Location trouvée avec l'ID: {}", requestId, id);
                return ResponseEntity.ok(rentalOpt.get());
            } else {
                log.warn("⚠️ [{}] Location non trouvée avec l'ID: {}", requestId, id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("❌ [{}] Erreur lors de la récupération de la location {}: {}", requestId, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur lors de la récupération de la location"));
        }
    }

   /**
    * Crée une nouvelle location.
    *
    * @param name Nom de la location
    * @param surface Surface de la location
    * @param price Prix de la location
    * @param description Description de la location
    * @param picture Image de la location
    * @param authentication Objet d'authentification fourni par Spring Security
    * @return Réponse contenant la location créée
     */
    @Operation(summary = "Crée une nouvelle location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la création de la location"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            Authentication authentication) {

        String requestId = UUID.randomUUID().toString();
        log.info("📥 [{}] Réception d'une requête de création de location: {}", requestId, name);

        // Vérifie que l'utilisateur est authentifié
        if (authentication == null || authentication.getName() == null) {
            log.error("⛔ [{}] Erreur: Utilisateur non authentifié", requestId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Utilisateur non authentifié"));
        }

        String userEmail = authentication.getName();
        log.debug("👤 [{}] Recherche de l'utilisateur avec l'email: {}", requestId, userEmail);
        
        try {
            // Récupération de l'utilisateur
            User owner = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        log.error("❌ [{}] Utilisateur non trouvé avec l'email: {}", requestId, userEmail);
                        return new UserNotFoundException("Utilisateur non trouvé avec l'email: " + userEmail);
                    });
            log.info("👤 [{}] Utilisateur authentifié: {}, ID: {}", requestId, owner.getEmail(), owner.getId());

            // Création du DTO
            RentalDto rentalDto = new RentalDto();
            rentalDto.setName(name);
            rentalDto.setSurface(surface);
            rentalDto.setPrice(price);
            rentalDto.setDescription(description);
            rentalDto.setOwnerId(owner.getId());
            log.debug("📦 [{}] Données du RentalDto préparées: {}", requestId, rentalDto.getName());

            // Gestion de l'image
            if (picture != null && !picture.isEmpty()) {
                try {
                    log.debug("🖼️ [{}] Traitement de l'image: {}, taille: {} octets", requestId, 
                            picture.getOriginalFilename(), picture.getSize());
                    String imagePath = fileStorageService.saveFile(picture);
                    rentalDto.setPicture(imagePath);
                    log.info("📸 [{}] Image enregistrée avec succès: {}", requestId, imagePath);
                } catch (IOException e) {
                    log.error("⚠️ [{}] Erreur lors de l'enregistrement de l'image: {}", requestId, e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Collections.singletonMap("message", "Erreur lors de l'enregistrement de l'image"));
                }
            } else {
                log.warn("⚠️ [{}] Aucune image fournie pour la location", requestId);
            }

            // Sauvegarde en base de données
            log.debug("💾 [{}] Appel du service pour créer la location", requestId);
            RentalDto createdRental = rentalService.createRental(rentalDto);

            // Vérifie que createdRental n'est pas null
            if (createdRental == null) {
                log.error("⛔ [{}] Erreur: RentalService a retourné null", requestId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("message", "Erreur lors de la création de la location"));
            }

            log.info("✅ [{}] Location créée avec succès, ID: {}", requestId, createdRental.getId());
            return ResponseEntity.ok(createdRental);
            
        } catch (UserNotFoundException e) {
            log.error("⛔ [{}] Utilisateur non trouvé: {}", requestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ [{}] Erreur inattendue lors de la création de la location: {}", requestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur lors de la création de la location"));
        }
    }

    /** 
     * Met à jour une location existante.
     * 
     * @param id ID de la location
     * @param name Nom de la location
     * @param surface Surface de la location
     * @param price Prix de la location
     * @param description Description de la location
     * @param picture Image de la location
     * @param authentication Objet d'authentification fourni par Spring Security
     * @return Réponse contenant la location mise à jour
     */
    @Operation(summary = "Met à jour une location existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de la mise à jour de la location"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié"),
            @ApiResponse(responseCode = "404", description = "Location non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRental(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surface", required = false) BigDecimal surface,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            Authentication authentication) {
    
        String requestId = UUID.randomUUID().toString();
        log.info("📥 [{}] Réception d'une requête de mise à jour de la location avec l'ID: {}", requestId, id);
    
        // Vérifie que l'utilisateur est authentifié
        if (authentication == null || authentication.getName() == null) {
            log.error("⛔ [{}] Erreur: Utilisateur non authentifié", requestId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Utilisateur non authentifié"));
        }
    
        String userEmail = authentication.getName();
        log.debug("👤 [{}] Utilisateur authentifié: {}", requestId, userEmail);
        
        try {
            // Récupération de la location existante
            log.debug("🔍 [{}] Recherche de la location avec l'ID: {}", requestId, id);
            Optional<RentalDto> existingRentalOpt = rentalService.getRentalById(id);
            
            if (existingRentalOpt.isEmpty()) {
                log.error("⛔ [{}] Location non trouvée avec l'ID: {}", requestId, id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "Location non trouvée avec l'ID: " + id));
            }
            
            RentalDto existingRental = existingRentalOpt.get();
            log.debug("✅ [{}] Location trouvée, préparation des modifications", requestId);
            
            // Mise à jour des champs si fournis
            boolean hasChanges = false;
            
            if (name != null && !name.equals(existingRental.getName())) {
                log.trace("✏️ [{}] Mise à jour du nom: {} -> {}", requestId, existingRental.getName(), name);
                existingRental.setName(name);
                hasChanges = true;
            }
            
            if (surface != null && !surface.equals(existingRental.getSurface())) {
                log.trace("✏️ [{}] Mise à jour de la surface: {} -> {}", requestId, existingRental.getSurface(), surface);
                existingRental.setSurface(surface);
                hasChanges = true;
            }
            
            if (price != null && !price.equals(existingRental.getPrice())) {
                log.trace("✏️ [{}] Mise à jour du prix: {} -> {}", requestId, existingRental.getPrice(), price);
                existingRental.setPrice(price);
                hasChanges = true;
            }
            
            if (description != null && !description.equals(existingRental.getDescription())) {
                log.trace("✏️ [{}] Mise à jour de la description", requestId);
                existingRental.setDescription(description);
                hasChanges = true;
            }
            
            // Gestion de l'image
            if (picture != null && !picture.isEmpty()) {
                try {
                    log.debug("🖼️ [{}] Traitement de la nouvelle image: {}, taille: {} octets", requestId, 
                            picture.getOriginalFilename(), picture.getSize());
                    String imagePath = fileStorageService.saveFile(picture);
                    existingRental.setPicture(imagePath);
                    log.info("📸 [{}] Nouvelle image enregistrée avec succès: {}", requestId, imagePath);
                    hasChanges = true;
                } catch (IOException e) {
                    log.error("⚠️ [{}] Erreur lors de l'enregistrement de la nouvelle image: {}", requestId, e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Collections.singletonMap("message", "Erreur lors de l'enregistrement de l'image"));
                }
            }
            
            if (!hasChanges) {
                log.info("ℹ️ [{}] Aucune modification détectée pour la location ID: {}", requestId, id);
                return ResponseEntity.ok(existingRental);
            }
            
            // Sauvegarde en base de données
            log.debug("💾 [{}] Appel du service pour mettre à jour la location", requestId);
            RentalDto updatedRental = rentalService.updateRental(id, existingRental);
            
            if (updatedRental == null) {
                log.error("⛔ [{}] Erreur: RentalService a retourné null lors de la mise à jour", requestId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("message", "Erreur lors de la mise à jour de la location"));
            }
            
            log.info("✅ [{}] Location mise à jour avec succès, ID: {}", requestId, updatedRental.getId());
            return ResponseEntity.ok(updatedRental);
            
        } catch (Exception e) {
            log.error("❌ [{}] Erreur inattendue lors de la mise à jour de la location: {}", requestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur lors de la mise à jour de la location"));
        }
    }

    /** 
     * Supprime une location existante.
     * 
     * @param id ID de la location
     * @return Réponse indiquant si la location a été supprimée avec succès
     */
    @Operation(summary = "Supprime une location existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Location supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Location non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRental(@PathVariable Long id) {
        String requestId = UUID.randomUUID().toString();
        log.info("📥 [{}] Réception d'une requête de suppression de la location avec l'ID: {}", requestId, id);
        
        try {
            // Vérifier d'abord que la location existe
            log.debug("🔍 [{}] Vérification de l'existence de la location avec l'ID: {}", requestId, id);
            Optional<RentalDto> existingRentalOpt = rentalService.getRentalById(id);
            
            if (existingRentalOpt.isEmpty()) {
                log.warn("⚠️ [{}] Tentative de suppression d'une location inexistante avec l'ID: {}", requestId, id);
                return ResponseEntity.notFound().build();
            }
            
            log.debug("🗑️ [{}] Suppression de la location avec l'ID: {}", requestId, id);
            rentalService.deleteRental(id);
            log.info("✅ [{}] Location supprimée avec succès, ID: {}", requestId, id);
            
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("❌ [{}] Erreur lors de la suppression de la location avec l'ID: {}: {}", 
                    requestId, id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur lors de la suppression de la location"));
        }
    }
}
