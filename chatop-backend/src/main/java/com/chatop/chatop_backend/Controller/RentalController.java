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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    }

    /**
     * Récupère toutes les locations.
     * 
     * @return Liste de toutes les locations
     */
    @Operation(summary = "Récupère toutes les locations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Locations récupérées avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucune location trouvée")
    })
    @GetMapping
    public ResponseEntity<?> getAllRentals() {
        // Debogage
        log.info("🚀 getAllRentals() - Récupération de toutes les locations");

        List<RentalDto> rentals = rentalService.getAllRentals();

        if (rentals.isEmpty()) {
            log.warn("⚠️ Aucune location trouvée");
            return ResponseEntity.noContent().build();
        } else {
            log.info("✅ {} locations trouvées", rentals.size());
            return ResponseEntity.ok(Collections.singletonMap("rentals", rentals));
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
        @ApiResponse(responseCode = "404", description = "Location non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
            @ApiResponse(responseCode = "400", description = "Erreur lors de la création de la location")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RentalDto> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") BigDecimal surface,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            Authentication authentication) {

        log.info("🚀 createRental() - Début de la création d'une location");

        // Vérifie que l'utilisateur est authentifié
        if (authentication == null || authentication.getName() == null) {
            log.error("⛔ Erreur: Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = authentication.getName();
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        log.info("👤 Utilisateur authentifié: {}", owner.getEmail());

        // Création du DTO
        RentalDto rentalDto = new RentalDto();
        rentalDto.setName(name);
        rentalDto.setSurface(surface);
        rentalDto.setPrice(price);
        rentalDto.setDescription(description);
        rentalDto.setOwnerId(owner.getId());

        log.info("📦 Données du RentalDto: {}", rentalDto);

        // Gestion de l'image
        if (picture != null && !picture.isEmpty()) {
            try {
                rentalDto.setPicture(fileStorageService.saveFile(picture));
                log.info("📸 Image enregistrée avec succès");
            } catch (IOException e) {
                log.error("⚠️ Erreur lors de l'enregistrement de l'image", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            log.warn("⚠️ Aucune image fournie");
        }

        // Sauvegarde en base de données
        RentalDto createdRental = rentalService.createRental(rentalDto);

        // 🔥 Vérifie que createdRental n'est pas null
        if (createdRental == null) {
            log.error("⛔ Erreur: RentalService a retourné null");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        log.info("✅ Location créée avec succès: {}", createdRental);
        return ResponseEntity.ok(createdRental);
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
            @ApiResponse(responseCode = "400", description = "Erreur lors de la mise à jour de la location")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RentalDto> updateRental(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surface", required = false) BigDecimal surface,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            Authentication authentication) {
    
        log.info("🚀 updateRental() - Début de la mise à jour de la location id={}", id);
    
        // Vérifie que l'utilisateur est authentifié
        if (authentication == null || authentication.getName() == null) {
            log.error("⛔ Erreur: Utilisateur non authentifié");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        // Récupération de la location existante
        Optional<RentalDto> existingRentalOpt = rentalService.getRentalById(id);
        if (existingRentalOpt.isEmpty()) {
            log.error("⛔ Location non trouvée avec l'id: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        RentalDto existingRental = existingRentalOpt.get();
        
        // Mise à jour des champs si fournis
        if (name != null) existingRental.setName(name);
        if (surface != null) existingRental.setSurface(surface);
        if (price != null) existingRental.setPrice(price);
        if (description != null) existingRental.setDescription(description);
        
        // Gestion de l'image
        if (picture != null && !picture.isEmpty()) {
            try {
                String imagePath = fileStorageService.saveFile(picture);
                existingRental.setPicture(imagePath);
                log.info("📸 Nouvelle image enregistrée avec succès");
            } catch (IOException e) {
                log.error("⚠️ Erreur lors de l'enregistrement de la nouvelle image", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        
        // Sauvegarde en base de données
        RentalDto updatedRental = rentalService.updateRental(id, existingRental);
        log.info("✅ Location mise à jour avec succès: {}", updatedRental);
        
        return ResponseEntity.ok(updatedRental);
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
            @ApiResponse(responseCode = "404", description = "Location non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}
