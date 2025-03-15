package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.RentalDto;

import java.util.List;
import java.util.Optional;

/**
 * Cette interface définit les méthodes que doit implémenter un service de gestion des locations.
 * Elle contient les méthodes pour créer, lire, mettre à jour et supprimer une location.
  */
public interface RentalService {

    // Créer une location sur base des informations fournies dans le DTO
    RentalDto createRental(RentalDto rentalDto);

    // Lire une location par son identifiant
    Optional<RentalDto> getRentalById(Long id);

    // Lire toutes les locations
    List<RentalDto> getAllRentals();

    // Mettre à jour une location
    RentalDto updateRental(Long id, RentalDto rentalDto);

    // Supprimer une location
    void deleteRental(Long id);
}
