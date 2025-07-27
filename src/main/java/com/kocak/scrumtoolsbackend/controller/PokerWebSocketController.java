package com.kocak.scrumtoolsbackend.controller;

import com.kocak.scrumtoolsbackend.dto.PokerMessageDto;
import com.kocak.scrumtoolsbackend.service.PokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class PokerWebSocketController {

    @Autowired
    private PokerService pokerService;

    @MessageMapping("/poker/team/{teamId}/join")
    @SendTo("/topic/poker/team/{teamId}")
    public PokerMessageDto joinPokerRoom(@DestinationVariable Long teamId,
                                        @Payload PokerMessageDto message,
                                        SimpMessageHeaderAccessor headerAccessor) {

        // Session'a kullanıcı bilgisini ekle
        headerAccessor.getSessionAttributes().put("teamId", teamId);
        headerAccessor.getSessionAttributes().put("userId", message.getUserId());

        message.setType(PokerMessageDto.MessageType.USER_JOINED);
        message.setMessage(message.getUserName() + " poker odasına katıldı");

        return message;
    }

    @MessageMapping("/poker/team/{teamId}/leave")
    @SendTo("/topic/poker/team/{teamId}")
    public PokerMessageDto leavePokerRoom(@DestinationVariable Long teamId,
                                         @Payload PokerMessageDto message) {

        message.setType(PokerMessageDto.MessageType.USER_LEFT);
        message.setMessage(message.getUserName() + " poker odasından ayrıldı");

        return message;
    }
}
