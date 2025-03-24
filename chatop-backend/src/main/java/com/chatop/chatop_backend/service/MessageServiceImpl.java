package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.MessageDto;
import com.chatop.chatop_backend.dto.ResponseMessage;
import com.chatop.chatop_backend.model.Message;
import com.chatop.chatop_backend.model.Rental;
import com.chatop.chatop_backend.model.User;
import com.chatop.chatop_backend.repository.MessageRepository;
import com.chatop.chatop_backend.repository.RentalRepository;
import com.chatop.chatop_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public ResponseMessage saveMessage(MessageDto dto) {
        String operationId = UUID.randomUUID().toString();
        log.info("📥 [{}] Début de la sauvegarde d'un message pour la location: {}", operationId, dto.getRentalId());
        
        try {
            // Validation des entrées
            validateMessageInput(operationId, dto);
            
            // Récupération de l'utilisateur
            User user = null;
            try {
                log.debug("🔍 [{}] Recherche de l'utilisateur avec ID: {}", operationId, dto.getUserId());
                user = userRepository.findById(dto.getUserId())
                        .orElseThrow(() -> {
                            log.error("❌ [{}] Utilisateur non trouvé avec ID: {}", operationId, dto.getUserId());
                            return new IllegalArgumentException("Utilisateur non trouvé avec ID: " + dto.getUserId());
                        });
                log.debug("✅ [{}] Utilisateur trouvé: {}", operationId, user.getEmail());
            } catch (Exception e) {
                log.error("❌ [{}] Erreur lors de la recherche de l'utilisateur: {}", operationId, e.getMessage(), e);
                throw new RuntimeException("Erreur lors de la recherche de l'utilisateur: " + e.getMessage(), e);
            }
            
            // Récupération de la location
            Rental rental = null;
            try {
                log.debug("🔍 [{}] Recherche de la location avec ID: {}", operationId, dto.getRentalId());
                rental = rentalRepository.findById(dto.getRentalId())
                        .orElseThrow(() -> {
                            log.error("❌ [{}] Location non trouvée avec ID: {}", operationId, dto.getRentalId());
                            return new IllegalArgumentException("Location non trouvée avec ID: " + dto.getRentalId());
                        });
                log.debug("✅ [{}] Location trouvée: {}", operationId, rental.getName());
            } catch (Exception e) {
                log.error("❌ [{}] Erreur lors de la recherche de la location: {}", operationId, e.getMessage(), e);
                throw new RuntimeException("Erreur lors de la recherche de la location: " + e.getMessage(), e);
            }
            
            // Création et sauvegarde du message
            Message message = null;
            try {
                log.debug("🔧 [{}] Création de l'objet Message", operationId);
                message = new Message();
                message.setUser(user);
                message.setRental(rental);
                message.setMessage(dto.getMessage());
                message.setCreatedAt(LocalDateTime.now());
                message.setUpdatedAt(LocalDateTime.now());
                
                log.debug("💾 [{}] Sauvegarde du message en base de données", operationId);
                message = messageRepository.save(message);
                log.info("✅ [{}] Message sauvegardé avec succès, ID: {}", operationId, message.getId());
            } catch (Exception e) {
                log.error("❌ [{}] Erreur lors de la sauvegarde du message: {}", operationId, e.getMessage(), e);
                throw new RuntimeException("Erreur lors de la sauvegarde du message: " + e.getMessage(), e);
            }
            
            log.info("🏁 [{}] Opération de sauvegarde du message terminée avec succès", operationId);
            return new ResponseMessage("Message envoyé avec succès");
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ [{}] Erreur de validation: {}", operationId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ [{}] Erreur inattendue lors de la sauvegarde du message: {}", operationId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la sauvegarde du message: " + e.getMessage(), e);
        }
    }

    /**
     * Valide les données d'entrée du message
     * 
     * @param operationId Identifiant de l'opération pour le logging
     * @param dto Message à valider
     * @throws IllegalArgumentException Si les données sont invalides
     */
    private void validateMessageInput(String operationId, MessageDto dto) {
        log.debug("🔍 [{}] Validation des données du message", operationId);
        
        if (dto == null) {
            log.error("❌ [{}] Le message ne peut pas être null", operationId);
            throw new IllegalArgumentException("Le message ne peut pas être null");
        }
        
        if (dto.getUserId() == null) {
            log.error("❌ [{}] L'ID de l'utilisateur ne peut pas être null", operationId);
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
        }
        
        if (dto.getRentalId() == null) {
            log.error("❌ [{}] L'ID de la location ne peut pas être null", operationId);
            throw new IllegalArgumentException("L'ID de la location ne peut pas être null");
        }
        
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
            log.error("❌ [{}] Le contenu du message ne peut pas être vide", operationId);
            throw new IllegalArgumentException("Le contenu du message ne peut pas être vide");
        }
        
        // Vérification de la longueur du message
        if (dto.getMessage().length() > 2000) { // Exemple de limite arbitraire
            log.warn("⚠️ [{}] Le message dépasse la limite de caractères autorisée", operationId);
            throw new IllegalArgumentException("Le message dépasse la limite de caractères autorisée (2000 max)");
        }
        
        log.debug("✅ [{}] Validation des données du message réussie", operationId);
    }

    @Override
    public List<MessageDto> getAllMessages() {
        String operationId = UUID.randomUUID().toString();
        log.info("📥 [{}] Début de récupération de tous les messages", operationId);
        
        List<MessageDto> messageDtos = new ArrayList<>();
        
        try {
            log.debug("🔍 [{}] Requête à la base de données pour récupérer tous les messages", operationId);
            List<Message> messages = messageRepository.findAll();
            
            if (messages.isEmpty()) {
                log.warn("⚠️ [{}] Aucun message trouvé dans la base de données", operationId);
                return messageDtos;
            }
            
            log.debug("🔄 [{}] Conversion des {} entités Message en DTOs", operationId, messages.size());
            messageDtos = messages.stream()
                    .map(message -> {
                        try {
                            return MessageDto.builder()
                                    .userId(message.getUser().getId())
                                    .rentalId(message.getRental().getId())
                                    .message(message.getMessage())
                                    .build();
                        } catch (Exception e) {
                            log.error("❌ [{}] Erreur lors de la conversion du message ID {}: {}", 
                                    operationId, message.getId(), e.getMessage());
                            // On continue malgré l'erreur sur un message spécifique
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            
            log.info("✅ [{}] Récupération et conversion de {} messages réussies", operationId, messageDtos.size());
            
            // Alerte si certains messages n'ont pas pu être convertis
            int difference = messages.size() - messageDtos.size();
            if (difference > 0) {
                log.warn("⚠️ [{}] {} messages n'ont pas pu être convertis en DTO", operationId, difference);
            }
            
            return messageDtos;
        } catch (Exception e) {
            log.error("❌ [{}] Erreur lors de la récupération des messages: {}", operationId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des messages: " + e.getMessage(), e);
        } finally {
            log.info("🏁 [{}] Fin de l'opération de récupération des messages", operationId);
        }
    }
}
