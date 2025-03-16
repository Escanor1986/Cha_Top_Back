package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.RentalDto;
import com.chatop.chatop_backend.model.Rental;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.RentalRepository;
import com.chatop.chatop_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cette classe implémente les méthodes définies dans l'interface RentalService.
 * Elle permet de gérer les locations en interagissant avec la base de données.
 * @Service: Indique à Spring qu'il s'agit d'un bean qui doit être instancié.
 * @Override: Indique que la méthode redéfinit une méthode de l'interface implémentée.
  */
@Service
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public RentalServiceImpl(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RentalDto createRental(RentalDto rentalDto) {
        Rental rental = mapToEntity(rentalDto);
        rental.setCreatedAt(LocalDateTime.now());
        rental.setUpdatedAt(LocalDateTime.now());
        
        // 🔥 Vérifier que l'utilisateur existe
        User owner = userRepository.findById(rentalDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + rentalDto.getOwnerId()));
        rental.setOwner(owner);

        Rental savedRental = rentalRepository.save(rental);
        return mapToDto(savedRental);
    }

    @Override
    public Optional<RentalDto> getRentalById(Long id) {
        return rentalRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RentalDto updateRental(Long id, RentalDto rentalDto) {
        Optional<Rental> optionalRental = rentalRepository.findById(id);
        if (optionalRental.isPresent()) {
            Rental rental = optionalRental.get();
            rental.setName(rentalDto.getName());
            rental.setSurface(rentalDto.getSurface());
            rental.setPrice(rentalDto.getPrice());
            rental.setPicture(rentalDto.getPicture());
            rental.setDescription(rentalDto.getDescription());
            rental.setUpdatedAt(LocalDateTime.now());
            Rental updatedRental = rentalRepository.save(rental);
            return mapToDto(updatedRental);
        }
        throw new RuntimeException("Rental not found with id: " + id);
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }

    // 🔹 Méthode de mapping : Entity -> DTO
    // 🔥 Cette méthode est utilisée pour convertir une entité Rental en DTO RentalDto.
    // 🔥 Elle est utilisée dans les méthodes de service pour retourner des objets DTO.
    private RentalDto mapToDto(Rental rental) {
        RentalDto dto = new RentalDto();
        dto.setId(rental.getId());
        dto.setName(rental.getName());
        dto.setSurface(rental.getSurface());
        dto.setPrice(rental.getPrice());
        // Vérifier que le chemin n'inclut pas déjà "/uploads/"
        String picturePath = rental.getPicture();
        // 🔥 Vérifier si l'image existe
        if (picturePath != null && !picturePath.isEmpty()) {
            // Vérifier que le chemin n'inclut pas déjà "/uploads/"
            if (!picturePath.startsWith("/uploads/")) {
                // Si le chemin ne commence pas par "/uploads/", ajouter "/uploads/"
                picturePath = "/uploads/" + picturePath;
            }
            // On ajoute le chemin complet de l'image
            dto.setPicture("http://localhost:3001" + rental.getPicture());
        } else {
            // 🔥 Retourner null si l'image n'existe pas
            dto.setPicture(null);
        }
        dto.setDescription(rental.getDescription());
        dto.setOwnerId(rental.getOwner().getId());
        return dto;
    }

    // 🔹 Méthode de mapping : DTO -> Entity
    private Rental mapToEntity(RentalDto dto) {
        Rental rental = new Rental();
        rental.setName(dto.getName());
        rental.setSurface(dto.getSurface());
        rental.setPrice(dto.getPrice());
        rental.setPicture(dto.getPicture());
        rental.setDescription(dto.getDescription());

        // 🔥 Vérifier l'existence du propriétaire
        if (dto.getOwnerId() != null) {
            User owner = userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found with id: " + dto.getOwnerId()));
            rental.setOwner(owner);
        }
        return rental;
    }
}
