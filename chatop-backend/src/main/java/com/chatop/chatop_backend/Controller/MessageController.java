package com.chatop.chatop_backend.controller;

import com.chatop.chatop_backend.dto.MessageDto;
import com.chatop.chatop_backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        messageService.saveMessage(messageDto);
        return ResponseEntity.ok("{\"message\": \"Message sent with success\"}");
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
}
