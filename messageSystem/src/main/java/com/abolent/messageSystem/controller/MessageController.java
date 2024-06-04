package com.abolent.messageSystem.controller;

import com.abolent.messageSystem.model.Message;
import com.abolent.messageSystem.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    private MessageRepository messageRepository;

    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        return messageRepository.save(message);
    }

    @GetMapping("/{receiverEmail}")
    public List<Message> getMessages(@PathVariable String receiverEmail) {
        return messageRepository.findByReceiverEmail(receiverEmail);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sent/{senderEmail}")
    public List<Message> getSentMessages(@PathVariable String senderEmail) {
        return messageRepository.findBySenderEmail(senderEmail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long id, @RequestBody Message updatedMessage) {
        return messageRepository.findById(id)
                .map(message -> {
                    message.setContent(updatedMessage.getContent());
                    Message savedMessage = messageRepository.save(message);
                    return ResponseEntity.ok(savedMessage);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
