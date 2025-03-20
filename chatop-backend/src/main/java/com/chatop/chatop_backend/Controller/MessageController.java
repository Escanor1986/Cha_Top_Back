package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.MessageDto;
import com.chatop.chatop_backend.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur pour gérer les messages.
 * 
 * @RestController: Indique à Spring qu'il s'agit d'un contrôleur REST.
 * @RequestMapping: Indique le préfixe commun pour toutes les routes définies dans ce contrôleur.
 * !Dans ce cas, toutes les routes commenceront par /api/messages.
 * @RequiredArgsConstructor: Génère un constructeur avec tous les arguments de la classe marqués comme @NonNull.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * Envoie un message.
     * 
     * @param messageDto Données du message à envoyer
     * @return Réponse indiquant si le message a été envoyé avec succès
      */
    @Operation(summary = "Envoie un message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur lors de l'envoi du message")
    })
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        messageService.saveMessage(messageDto);
        return ResponseEntity.ok("{\"message\": \"Message sent with success\"}");
    }

    /**
     * Récupère tous les messages.
     * 
     * @return Liste de tous les messages
     */
    @Operation(summary = "Récupère tous les messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucun message trouvé")
    })
    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
}
