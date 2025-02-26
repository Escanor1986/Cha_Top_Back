package com.chatop.chatop_backend.model;

import jakarta.persistence.*;
// lombok sert à générer automatiquement les getters, setters, constructeurs, equals, hashcode, toString. Il permet également de réduire la quantité de code à écrire.
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Entity: Spécifie que la classe est une entité.
 * @Table: Spécifie le nom de la table dans la base de données.
 * @Getter: Génère automatiquement les getters pour tous les champs de la classe.
 * @Setter: Génère automatiquement les setters pour tous les champs de la classe.
 * @NoArgsConstructor: Génère automatiquement un constructeur sans argument.
 * @AllArgsConstructor: Génère automatiquement un constructeur avec tous les arguments.
 */

@Entity
@Table(name = "RENTALS")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal surface;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private String picture;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
