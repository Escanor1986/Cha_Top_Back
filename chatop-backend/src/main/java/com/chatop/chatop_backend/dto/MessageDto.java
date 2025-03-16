package com.chatop.chatop_backend.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class MessageDto {
    private Long id;
    private Long rentalId; // Correspond à rental.getId()
    private Long userId;   // Correspond à user.getId()
    private String message;
    private String createdAt; // createdAt est retourné en String (format ISO 8601) pour éviter les problèmes de formatage.
}
