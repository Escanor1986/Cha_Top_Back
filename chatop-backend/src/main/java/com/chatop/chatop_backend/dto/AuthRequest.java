// DTO (data transfert object) est un objet qui transporte des données entre les processus.
// Ils sont utilisés dans la couche service pour transporter les données.
//  Ils permettent d'éviter d'exposer directement les entités JPA dans la couche service.
//  Ils permettent de contrôler les données qui sont envoyées ou reçues par l'API.

package com.chatop.chatop_backend.dto;

import lombok.Data;
/**
 * @Data: Génère automatiquement les getters, setters, constructeurs, equals, hashcode, toString.
 * Cette classe est utilisée pour stocker les informations d'authentification de l'utilisateur.
 */
@Data
public class AuthRequest {
    private String email;
    private String password;
}
