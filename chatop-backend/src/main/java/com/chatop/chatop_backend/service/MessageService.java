package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.MessageDto;
import java.util.List;

public interface MessageService {
    MessageDto saveMessage(MessageDto messageDto); // Enregistre un message dans la base de données.
    List<MessageDto> getAllMessages(); // Récupère tous les messages de la base de données.
}
