package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.MessageDto;
import com.chatop.chatop_backend.dto.ResponseMessage;
import com.chatop.chatop_backend.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
@Tag(name = "Messages", description = "APIs pour gérer les messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
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
            @ApiResponse(responseCode = "400", description = "Données du message invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur ou location non trouvés"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        String requestId = UUID.randomUUID().toString();
        log.info("📥 [{}] Réception d'une requête d'envoi de message pour la location ID: {}", 
                requestId, messageDto != null ? messageDto.getRentalId() : "null");
        
        try {
            // Le service s'occupe de la validation complète
            log.debug("🔍 [{}] Transmission du message au service pour traitement", requestId);
            ResponseMessage response = messageService.saveMessage(messageDto);
            
            log.info("✅ [{}] Message envoyé avec succès", requestId);
            return ResponseEntity.ok(Collections.singletonMap("message", "Message sent with success"));
            
        } catch (IllegalArgumentException e) {
            // Erreur de validation ou données non trouvées (provenant du service)
            log.warn("⚠️ [{}] Erreur de validation: {}", requestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", e.getMessage()));
            
        } catch (Exception e) {
            // Erreur inattendue
            log.error("❌ [{}] Erreur inattendue lors de l'envoi du message: {}", requestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur lors de l'envoi du message"));
            
        } finally {
            log.info("🏁 [{}] Fin du traitement de la requête d'envoi de message", requestId);
        }
    }

    /**
     * Récupère tous les messages.
     * 
     * @return Liste de tous les messages
     */
    @Operation(summary = "Récupère tous les messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages récupérés avec succès"),
            @ApiResponse(responseCode = "204", description = "Aucun message trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    public ResponseEntity<?> getMessages() {
        String requestId = UUID.randomUUID().toString();
        log.info("📥 [{}] Réception d'une requête de récupération de tous les messages", requestId);
        
        try {
            log.debug("🔍 [{}] Appel du service pour récupérer tous les messages", requestId);
            List<MessageDto> messages = messageService.getAllMessages();
            
            if (messages == null || messages.isEmpty()) {
                log.warn("⚠️ [{}] Aucun message trouvé", requestId);
                return ResponseEntity.noContent().build();
            }
            
            log.info("✅ [{}] {} messages récupérés avec succès", requestId, messages.size());
            return ResponseEntity.ok(messages);
            
        } catch (Exception e) {
            log.error("❌ [{}] Erreur lors de la récupération des messages: {}", requestId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Erreur lors de la récupération des messages"));
            
        } finally {
            log.info("🏁 [{}] Fin du traitement de la requête de récupération des messages", requestId);
        }
    }
}
