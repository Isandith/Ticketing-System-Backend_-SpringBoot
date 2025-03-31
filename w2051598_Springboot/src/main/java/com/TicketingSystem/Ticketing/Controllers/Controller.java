package com.TicketingSystem.Ticketing.Controllers;


import com.TicketingSystem.Ticketing.Services.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private ConfigService configService;
    private final SimpMessagingTemplate messagingTemplate;

    public Controller(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(Message message) {
        // Send the message to the "/topic/messages" destination
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
