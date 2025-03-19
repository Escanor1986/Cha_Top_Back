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
import java.util.List;
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
        log.debug("📝 Sauvegarde du message: {}", dto);
        
        // Vérification des IDs
        if (dto.getUserId() == null) {
            log.error("❌ L'ID de l'utilisateur ne peut pas être null");
            throw new IllegalArgumentException("L'ID de l'utilisateur ne peut pas être null");
        }
        
        if (dto.getRentalId() == null) {
            log.error("❌ L'ID de la location ne peut pas être null");
            throw new IllegalArgumentException("L'ID de la location ne peut pas être null");
        }
        
        log.debug("🔍 Recherche de l'utilisateur avec ID: {}", dto.getUserId());
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> {
                    log.error("❌ Utilisateur non trouvé avec ID: {}", dto.getUserId());
                    return new IllegalArgumentException("Utilisateur non trouvé");
                });
        
        log.debug("🔍 Recherche de la location avec ID: {}", dto.getRentalId());
        Rental rental = rentalRepository.findById(dto.getRentalId())
                .orElseThrow(() -> {
                    log.error("❌ Location non trouvée avec ID: {}", dto.getRentalId());
                    return new IllegalArgumentException("Location non trouvée");
                });
        
        // Création et sauvegarde du message (sans utiliser builder)
        Message message = new Message();
        message.setUser(user);
        message.setRental(rental);
        message.setMessage(dto.getMessage());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        messageRepository.save(message);
        log.info("✅ Message sauvegardé avec succès");
        
        return new ResponseMessage("Message envoyé avec succès");
    }

    @Override
    public List<MessageDto> getAllMessages() {
        log.debug("📋 Récupération de tous les messages");
        List<Message> messages = messageRepository.findAll();
        
        if (messages.isEmpty()) {
            log.warn("⚠️ Aucun message trouvé");
        } else {
            log.info("✅ {} messages trouvés", messages.size());
        }
        
        // Convertir les entités Message en DTO
        return messages.stream()
                .map(message -> MessageDto.builder()
                        .userId(message.getUser().getId())
                        .rentalId(message.getRental().getId())
                        .message(message.getMessage())
                        .build())
                .collect(Collectors.toList());
    }
}
