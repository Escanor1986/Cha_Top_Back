package com.chatop.chatop_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    
    @JsonProperty("rental_id")
    private Long rentalId;
    
    @JsonProperty("user_id")
    private Long userId;
    
    private String message;
}
