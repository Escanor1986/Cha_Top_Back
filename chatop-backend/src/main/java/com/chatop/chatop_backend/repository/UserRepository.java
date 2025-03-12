package com.chatop.chatop_backend.repository;

import com.chatop.chatop_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour accéder aux données des utilisateurs.
 * Cette interface est utilisée par Spring Data JPA pour générer les requêtes SQL.
 * Elle hérite de l'interface JpaRepository qui contient les méthodes CRUD de base.
 * @Repository permet de déclarer ce composant comme un bean Spring.
 * Les méthodes de cette interface sont utilisées par le service AuthService pour accéder aux données des utilisateurs.
 * @see com.chatop.chatop_backend.service.AuthService
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Recherche un utilisateur par son email.
     *
     * @param email Email de l'utilisateur
     * @return Utilisateur correspondant à l'email
     */
    Optional<User> findByEmail(String email);
}
