// DTO (data transfert object) est un objet qui transporte des données entre les processus.
// Ils sont utilisés dans la couche service pour transporter les données.
//  Ils permettent d'éviter d'exposer directement les entités JPA dans la couche service.
//  Ils permettent de contrôler les données qui sont envoyées ou reçues par l'API.

package com.chatop.chatop_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cette classe est utilisée pour stocker les informations d'inscription de l'utilisateur.
 * @Getter: Génère automatiquement les getters pour tous les champs de la classe grâce à Lombok.
 * @Setter: Génère automatiquement les setters pour tous les champs de la classe grâce à Lombok.
 * @NoArgsConstructor: Génère automatiquement un constructeur sans argument grâce à Lombok.
 * @AllArgsConstructor: Génère automatiquement un constructeur avec tous les arguments grâce à Lombok également.
  */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public class AuthResponse {
      private String token;
      private Long id;
      private String email;
      private String name;
  }
