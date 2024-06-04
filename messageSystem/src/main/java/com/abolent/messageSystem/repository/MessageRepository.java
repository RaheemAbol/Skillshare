package com.abolent.messageSystem.repository;

import com.abolent.messageSystem.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverEmail(String receiverEmail);
    List<Message> findBySenderEmail(String senderEmail);
}
