package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.RentalDto;
import com.chatop.chatop_backend.service.FileStorageService;
import com.chatop.chatop_backend.service.RentalService;
import com.chatop.chatop_backend.repository.UserRepository;
import com.chatop.chatop_backend.model.User;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
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

    private final FileStorageService fileStorageService;
    private final RentalService rentalService;
    private final UserRepository userRepository;

    public RentalController(RentalService rentalService, FileStorageService fileStorageService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository; 
    }

    @GetMapping
    public ResponseEntity<List<RentalDto>> getAllRentals() {
        List<RentalDto> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> getRentalById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Correction : Suppression du @RequestParam ownerId
    // 🔹 Ajout de l'extraction automatique de l'utilisateur depuis l'authentification (JWT)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<RentalDto> createRental(
        @RequestParam("name") String name,
        @RequestParam("surface") BigDecimal surface,
        @RequestParam("price") BigDecimal price,
        @RequestParam("description") String description,
        @RequestParam(value = "picture", required = false) MultipartFile picture,
        Authentication authentication) {

    // 🔥 Récupérer l'email de l'utilisateur depuis l'objet Authentication
    String userEmail = authentication.getName(); // L'email est stocké dans le JWT
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email: " + userEmail));

    RentalDto rentalDto = new RentalDto();
    rentalDto.setName(name);
    rentalDto.setSurface(surface);
    rentalDto.setPrice(price);
    rentalDto.setDescription(description);
    rentalDto.setOwnerId(user.getId()); // ✅ Correctement défini

    if (picture != null && !picture.isEmpty()) {
        try {
            rentalDto.setPicture(fileStorageService.saveFile(picture));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // Erreur interne
        }
    }

    RentalDto createdRental = rentalService.createRental(rentalDto);
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
