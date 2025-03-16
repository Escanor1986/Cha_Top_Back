package com.chatop.chatop_backend.repository;

import com.chatop.chatop_backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
