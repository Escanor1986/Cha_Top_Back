package com.chatop.chatop_backend.repository;

import com.chatop.chatop_backend.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Cette interface permet d'effectuer des opérations de lecture et d'écriture sur la table des locations.
 * Elle hérite de l'interface JpaRepository qui contient les méthodes de base pour interagir avec la base de données.
 * @Repository: Indique à Spring qu'il s'agit d'un bean qui doit être instancié.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {}
