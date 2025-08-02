package com.kocak.scrumtoolsbackend.controller;

import com.kocak.scrumtoolsbackend.dto.PokerMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class PokerWebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        // Bağlantı kurulduğunda özel bir işlem yapmaya gerek yok
        // Çünkü kullanıcı bilgileri join mesajı ile gelecek
        System.out.println("WebSocket bağlantısı kuruldu: " + event.toString());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Session'dan kullanıcı bilgilerini al
        Long teamId = (Long) headerAccessor.getSessionAttributes().get("teamId");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String userName = (String) headerAccessor.getSessionAttributes().get("userName");

        if (teamId != null && userId != null && userName != null) {
            // Takıma kullanıcının ayrıldığını bildir
            PokerMessageDto message = new PokerMessageDto();
            message.setType(PokerMessageDto.MessageType.USER_LEFT);
            message.setUserId(userId);
            message.setUserName(userName);
            message.setMessage(userName + " poker odasından ayrıldı (bağlantı koptu)");

            messagingTemplate.convertAndSend("/topic/poker/team/" + teamId, message);

            System.out.println("Kullanıcı poker odasından ayrıldı: " + userName + " (Team: " + teamId + ")");
        }
    }
}
