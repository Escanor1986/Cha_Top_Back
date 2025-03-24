package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.RentalDto;
import com.chatop.chatop_backend.model.Rental;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.RentalRepository;
import com.chatop.chatop_backend.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cette classe implémente les méthodes définies dans l'interface RentalService.
 * Elle permet de gérer les locations en interagissant avec la base de données.
 * 
 * @Service: Indique à Spring qu'il s'agit d'un bean qui doit être instancié.
 * @Override: Indique que la méthode redéfinit une méthode de l'interface
 *            implémentée.
 */
@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(RentalServiceImpl.class);

    public RentalServiceImpl(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crée une nouvelle location dans la base de données.
     * 
     * @param rentalDto Données de la location à créer
     * @return Location créée
      */
    @Override
    public RentalDto createRental(RentalDto rentalDto) {
        log.info("📝 Création d'une nouvelle location: {}", rentalDto.getName());
        try {

            Rental rental = mapToEntity(rentalDto);
            rental.setCreatedAt(LocalDateTime.now());
            rental.setUpdatedAt(LocalDateTime.now());

            // 🔥 Vérifier que l'utilisateur existe
            User owner = userRepository.findById(rentalDto.getOwnerId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + rentalDto.getOwnerId()));
            rental.setOwner(owner);
            
            Rental savedRental = rentalRepository.save(rental);
            return mapToDto(savedRental);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création de la location: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la création de la location");
        } finally {
            log.info("🔑 Location créée avec succès: {}", rentalDto.getName());
        }
    }

    /**
     * Récupère une location à partir de son ID.
     * 
     * @param id ID de la location à récupérer
     * @return Location trouvée
      */
    @Override
    public Optional<RentalDto> getRentalById(Long id) {
        log.info("🔍 Recherche de la location avec l'ID: {}", id);
        try {
            return rentalRepository.findById(id).map(this::mapToDto);
        } catch (Exception e) {
            log.error("❌ Location non trouvée avec l'ID: {}", id);
            throw new RuntimeException("Location non trouvée avec l'ID: " + id);
        } finally {
            log.info("✅ Location trouvée avec succès: {}", id);
        }
    }

    /**
     * Récupère toutes les locations de la base de données.
     * 
     * @return Liste de toutes les locations
      */
    @Override
    public List<RentalDto> getAllRentals() {
        log.info("🔍 Récupération de toutes les locations");
        try {
            return rentalRepository.findAll()
                    .stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des locations: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des locations");
        } finally {
            log.info("✅ Locations récupérées avec succès");
        }
    }

    /**
     * Met à jour une location existante dans la base de données.
     * 
     * @param id ID de la location à mettre à jour
     * @param rentalDto Données de la location à mettre à jour
     * @return Location mise à jour
      */
    @Override
    public RentalDto updateRental(Long id, RentalDto rentalDto) {
        log.info("🔄 Mise à jour de la location avec l'ID: {}", id);
        try {

            Optional<Rental> optionalRental = rentalRepository.findById(id);
            if (optionalRental.isPresent()) {
                log.info("✅ Location trouvée, mise à jour des données en cours...");
                Rental rental = optionalRental.get();
                rental.setName(rentalDto.getName());
                rental.setSurface(rentalDto.getSurface());
                rental.setPrice(rentalDto.getPrice());
                rental.setPicture(rentalDto.getPicture());
                rental.setDescription(rentalDto.getDescription());
                rental.setUpdatedAt(LocalDateTime.now());
                Rental updatedRental = rentalRepository.save(rental);
                return mapToDto(updatedRental);
            } else {
                log.error("❌ Location non trouvée avec l'ID: {}", id);
                throw new RuntimeException("Location non trouvée avec l'ID: " + id);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la mise à jour de la location: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la mise à jour de la location avec l'id: " + id);
        } finally {
            log.info("🔑 Location mise à jour avec succès: {}", rentalDto.getName());
        }
    }

    /**
     * Supprime une location de la base de données à partir de son ID.
     * 
     * @param id ID de la location à supprimer
      */
    @Override
    public void deleteRental(Long id) {
        log.info("🗑️ Suppression de la location avec l'ID: {}", id);
        try {    
            Optional<Rental> optionalRental = rentalRepository.findById(id);
            if (optionalRental.isPresent()) {
                log.info("✅ Location trouvée, suppression en cours...");
                rentalRepository.deleteById(id);
            } else {
                log.error("❌ Location non trouvée avec l'ID: {}", id);
                throw new RuntimeException("Location non trouvée avec l'ID: " + id);
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression de la location: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de la location avec l'id: " + id);
        } finally {
            log.info("🔑 Location supprimée avec succès: {}", id);
        }
    }

   // 🔹 Méthode de mapping : Entity -> DTO
// 🔥 Cette méthode est utilisée pour convertir une entité Rental en DTO RentalDto.
private RentalDto mapToDto(Rental rental) {
    log.debug("📊 Début du mapping Entity -> DTO pour la location: {}", rental.getId());
    try {
        RentalDto dto = new RentalDto();
        dto.setId(rental.getId());
        dto.setName(rental.getName());
        dto.setSurface(rental.getSurface());
        dto.setPrice(rental.getPrice());
        
        // Vérifier que le chemin n'inclut pas déjà "/uploads/"
        String picturePath = rental.getPicture();
        if (picturePath != null && !picturePath.isEmpty()) {
            log.trace("🖼️ Traitement du chemin d'image: {}", picturePath);
            // Vérifier si l'URL contient déjà http://localhost:3001
            if (picturePath.startsWith("http://localhost:3001")) {
                dto.setPicture(picturePath);
            } else {
                // Si le chemin ne commence pas par "/uploads/", ajouter "/uploads/"
                if (!picturePath.startsWith("/uploads/")) {
                    picturePath = "/uploads/" + picturePath;
                    log.trace("🖼️ Ajout du préfixe '/uploads/' au chemin d'image");
                }
                // Ajouter le préfixe du serveur
                dto.setPicture("http://localhost:3001" + picturePath);
                log.trace("🖼️ URL d'image complète générée: {}", dto.getPicture());
            }
        } else {
            dto.setPicture(null);
            log.trace("🖼️ Aucune image fournie pour cette location");
        }
        
        dto.setDescription(rental.getDescription());

        // Conversion LocalDateTime -> Date pour correspondre à l'interface Angular
        try {
            if (rental.getCreatedAt() != null) {
                dto.setCreatedAt(java.util.Date.from(
                        rental.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()));
                log.trace("📅 Date de création convertie avec succès");
            }

            if (rental.getUpdatedAt() != null) {
                dto.setUpdatedAt(java.util.Date.from(
                        rental.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant()));
                log.trace("📅 Date de mise à jour convertie avec succès");
            }
        } catch (Exception e) {
            log.warn("⚠️ Erreur lors de la conversion des dates: {}", e.getMessage());
        }
        
        dto.setOwnerId(rental.getOwner().getId());
        log.debug("✅ Mapping Entity -> DTO terminé avec succès pour la location: {}", rental.getId());
        return dto;
    } catch (NullPointerException e) {
        log.error("❌ NullPointerException lors du mapping Entity -> DTO: {}", e.getMessage());
        throw new RuntimeException("Erreur lors du mapping: données manquantes dans l'entité Rental", e);
    } catch (Exception e) {
        log.error("❌ Erreur inattendue lors du mapping Entity -> DTO: {}", e.getMessage());
        throw new RuntimeException("Erreur lors du mapping Entity -> DTO", e);
    }
}

// 🔹 Méthode de mapping : DTO -> Entity
// 🔥 Cette méthode est utilisée pour convertir un objet RentalDto en entité Rental.
private Rental mapToEntity(RentalDto dto) {
    log.debug("📊 Début du mapping DTO -> Entity pour la location: {}", dto.getName());
    try {
        Rental rental = new Rental();
        rental.setName(dto.getName());
        rental.setSurface(dto.getSurface());
        rental.setPrice(dto.getPrice());

        // Gestion de l'image - extraction du chemin relatif
        String picturePath = dto.getPicture();
        if (picturePath != null && !picturePath.isEmpty()) {
            log.trace("🖼️ Traitement du chemin d'image: {}", picturePath);
            // Nettoyer les URL dupliquées en supprimant toutes les instances de http://localhost:3001
            int initialLength = picturePath.length();
            while (picturePath.contains("http://localhost:3001")) {
                picturePath = picturePath.replace("http://localhost:3001", "");
            }
            if (initialLength != picturePath.length()) {
                log.trace("🖼️ URL nettoyée des préfixes serveur");
            }

            // S'assurer que le chemin commence par /uploads/
            if (!picturePath.startsWith("/uploads/")) {
                picturePath = "/uploads/" + picturePath;
                log.trace("🖼️ Ajout du préfixe '/uploads/' au chemin d'image");
            }

            rental.setPicture(picturePath);
            log.trace("🖼️ Chemin d'image final: {}", picturePath);
        } else {
            rental.setPicture(null);
            log.trace("🖼️ Aucune image fournie pour cette location");
        }
        
        rental.setDescription(dto.getDescription());

        // 🔥 Vérifier l'existence du propriétaire
        try {
            if (dto.getOwnerId() != null) {
                log.trace("👤 Recherche du propriétaire avec l'ID: {}", dto.getOwnerId());
                User owner = userRepository.findById(dto.getOwnerId())
                        .orElseThrow(() -> {
                            log.warn("⚠️ Propriétaire non trouvé avec l'ID: {}", dto.getOwnerId());
                            return new RuntimeException("Owner not found with id: " + dto.getOwnerId());
                        });
                rental.setOwner(owner);
                log.trace("👤 Propriétaire trouvé et associé à la location");
            } else {
                log.warn("⚠️ Aucun ID de propriétaire fourni dans le DTO");
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la recherche du propriétaire: {}", e.getMessage());
        }

        // Conversion Date -> LocalDateTime
        try {
            if (dto.getCreatedAt() != null) {
                rental.setCreatedAt(dto.getCreatedAt().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime());
                log.trace("📅 Date de création convertie avec succès");
            }

            if (dto.getUpdatedAt() != null) {
                rental.setUpdatedAt(dto.getUpdatedAt().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime());
                log.trace("📅 Date de mise à jour convertie avec succès");
            }
        } catch (Exception e) {
            log.warn("⚠️ Erreur lors de la conversion des dates: {}", e.getMessage());
        }

        log.debug("✅ Mapping DTO -> Entity terminé avec succès pour la location: {}", dto.getName());
        return rental;
    } catch (NullPointerException e) {
        log.error("❌ NullPointerException lors du mapping DTO -> Entity: {}", e.getMessage());
        throw new RuntimeException("Erreur lors du mapping: données manquantes dans le DTO", e);
    } catch (Exception e) {
        log.error("❌ Erreur inattendue lors du mapping DTO -> Entity: {}", e.getMessage());
        throw new RuntimeException("Erreur lors du mapping DTO -> Entity", e);
    }
}
}
