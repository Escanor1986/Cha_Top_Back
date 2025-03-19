package com.chatop.chatop_backend.service;

import com.chatop.chatop_backend.dto.MessageDto;
import com.chatop.chatop_backend.dto.ResponseMessage;

import java.util.List;

public interface MessageService {
    
    ResponseMessage saveMessage(MessageDto dto);
    
    List<MessageDto> getAllMessages();
}
