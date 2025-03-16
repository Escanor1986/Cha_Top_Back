package com.chatop.chatop_backend.model;

import jakarta.persistence.*;
// lombok sert à générer automatiquement les getters, setters, constructeurs, equals, hashcode, toString. Il permet également de réduire la quantité de code à écrire.
import lombok.*;

import java.time.LocalDateTime;

/**
 * @Entity: Spécifie que la classe est une entité.
 * @Table: Spécifie le nom de la table dans la base de données.
 * @Getter: Génère automatiquement les getters pour tous les champs de la
 *          classe.
 * @Setter: Génère automatiquement les setters pour tous les champs de la
 *          classe.
 * @NoArgsConstructor: Génère automatiquement un constructeur sans argument.
 * @AllArgsConstructor: Génère automatiquement un constructeur avec tous les
 *                      arguments.
 */

 @Entity
 @Table(name = "MESSAGES")
 @Getter @Setter
 @NoArgsConstructor @AllArgsConstructor
 public class Message {
 
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
 
   @ManyToOne
   @JoinColumn(name = "rental_id", nullable = false) // Clé étrangère vers Rental
   private Rental rental;
 
   @ManyToOne 
   @JoinColumn(name = "user_id", nullable = false) // Clé étrangère vers User
   private User user;
 
   @Column(columnDefinition = "TEXT", nullable = false)
   private String message;
 
   @Column(name = "created_at", updatable = false)
   private LocalDateTime createdAt = LocalDateTime.now();
 
   @Column(name = "updated_at")
   private LocalDateTime updatedAt;
 
   @PreUpdate
   protected void onUpdate() {
       updatedAt = LocalDateTime.now();
   }
 }
