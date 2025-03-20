package com.chatop.chatop_backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Cette classe est un Data Transfer Object (DTO) qui permet de transférer des données entre le client et le serveur.
 *  Elle contient les informations à envoyer au client concernant une location.
 *  @Data: Génère automatiquement les getters, setters, constructeurs, equals, hashcode, toString.
 * @NoArgsConstructor: Génère automatiquement un constructeur sans argument.
 * @AllArgsConstructor: Génère automatiquement un constructeur avec tous les arguments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private Long id;
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private String picture;
    private String description;
    
    @JsonProperty("created_at")
    private Date createdAt;
    
    @JsonProperty("updated_at")
    private Date updatedAt;
    
    @JsonProperty("owner_id")
    private Long ownerId;
}
