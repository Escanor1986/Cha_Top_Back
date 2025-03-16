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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Cette classe est un contrôleur REST qui expose les différentes routes pour
 * gérer les locations.
 * Elle permet de gérer les requêtes HTTP pour les locations.
 * 
 * @RestController: Indique à Spring qu'il s'agit d'un contrôleur REST.
 * @RequestMapping: Indique le préfixe commun pour toutes les routes définies
 *                  dans ce contrôleur.
 *                  !Dans ce cas, toutes les routes commenceront par
 *                  /api/rentals.
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

    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Correction : Suppression du @RequestParam ownerId
    // 🔹 Ajout de l'extraction automatique de l'utilisateur depuis
    // l'authentification (JWT)
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

    @PutMapping("/{id}")
    public ResponseEntity<RentalDto> updateRental(@PathVariable Long id, @RequestBody RentalDto rentalDto) {
        RentalDto updatedRental = rentalService.updateRental(id, rentalDto);
        return ResponseEntity.ok(updatedRental);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }
}
