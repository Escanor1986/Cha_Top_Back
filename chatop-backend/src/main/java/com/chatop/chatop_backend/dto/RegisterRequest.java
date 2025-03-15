// DTO (data transfert object) est un objet qui transporte des données entre les processus.
// Ils sont utilisés dans la couche service pour transporter les données.
//  Ils permettent d'éviter d'exposer directement les entités JPA dans la couche service.
//  Ils permettent de contrôler les données qui sont envoyées ou reçues par l'API.

package com.chatop.chatop_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class RegisterRequest {
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(min = 3, max = 50, message = "Le nom doit contenir entre 3 et 50 caractères")
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
}
