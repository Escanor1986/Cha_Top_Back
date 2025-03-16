package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.MessageDto;
import com.chatop.chatop_backend.model.Message;
import com.chatop.chatop_backend.repository.MessageRepository;
import com.chatop.chatop_backend.repository.RentalRepository;
import com.chatop.chatop_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    @Override
    public MessageDto saveMessage(MessageDto messageDto) {
        Message message = new Message();
        message.setMessage(messageDto.getMessage());
        message.setRental(rentalRepository.findById(messageDto.getRentalId()).orElseThrow());
        message.setUser(userRepository.findById(messageDto.getUserId()).orElseThrow());

        message = messageRepository.save(message);
        return mapToDto(message);
    }

    @Override
    public List<MessageDto> getAllMessages() {
        return messageRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private MessageDto mapToDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getRental().getId(),
                message.getUser().getId(),
                message.getMessage(),
                message.getCreatedAt().toString()
        );
    }
}
