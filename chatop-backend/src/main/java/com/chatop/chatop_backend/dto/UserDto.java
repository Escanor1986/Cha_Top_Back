package com.chatop.chatop_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * La classe UserDto est un Data Transfer Object (DTO) qui est utilisé pour transférer les données de l'utilisateur
 * entre les couches de l'application.
 * Il contient les attributs suivants:
 * - id: l'identifiant de l'utilisateur
 * - name: le nom de l'utilisateur
 * - email: l'adresse email de l'utilisateur
 * - createdAt: la date de création de l'utilisateur
 * - updatedAt: la date de mise à jour de l'utilisateur
 * Ces attributs sont accessibles via les méthodes getters et setters.
 * @Getter: génère les getters pour tous les attributs
 * @Setter: génère les setters pour tous les attributs
 * @NoArgsConstructor: génère un constructeur sans argument
 * @AllArgsConstructor: génère un constructeur avec tous les arguments
  */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
