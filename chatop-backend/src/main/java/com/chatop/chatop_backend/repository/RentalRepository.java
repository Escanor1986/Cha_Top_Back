package com.chatop.chatop_backend.repository;

import com.chatop.chatop_backend.model.Rental;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Cette interface permet d'effectuer des opérations de lecture et d'écriture sur la table des locations.
 * Elle hérite de l'interface JpaRepository qui contient les méthodes de base pour interagir avec la base de données.
 * @Repository: Indique à Spring qu'il s'agit d'un bean qui doit être instancié.
 */
@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

  // 🔥 Utilisation de l'annotation @EntityGraph pour charger l'entité propriétaire de la location.
  // Cela permet d'éviter les requêtes supplémentaires lors de l'accès à l'entité propriétaire.
  @EntityGraph(attributePaths = {"owner"})
  List<Rental> findAll();
}

