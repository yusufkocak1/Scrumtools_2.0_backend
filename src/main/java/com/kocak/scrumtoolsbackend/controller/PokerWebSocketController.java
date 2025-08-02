package com.kocak.scrumtoolsbackend.controller;

import com.kocak.scrumtoolsbackend.dto.PokerMessageDto;
import com.kocak.scrumtoolsbackend.dto.PokerSessionDto;
import com.kocak.scrumtoolsbackend.dto.PokerVoteRequest;
import com.kocak.scrumtoolsbackend.service.PokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class PokerWebSocketController {

    @Autowired
    private PokerService pokerService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/poker/team/{teamId}/join")
    @SendTo("/topic/poker/team/{teamId}")
    public PokerMessageDto joinPokerRoom(@DestinationVariable Long teamId,
                                        @Payload PokerMessageDto message,
                                        SimpMessageHeaderAccessor headerAccessor) {

        // Session'a kullanıcı bilgisini ekle
        headerAccessor.getSessionAttributes().put("teamId", teamId);
        headerAccessor.getSessionAttributes().put("userId", message.getUserId());
        headerAccessor.getSessionAttributes().put("userName", message.getUserName());

        // Frontend'den gelen userEmail bilgisini session'a kaydet
        if (message.getData() != null && message.getData().toString().contains("@")) {
            String userEmail = message.getData().toString();
            headerAccessor.getSessionAttributes().put("userEmail", userEmail);
        }

        message.setType(PokerMessageDto.MessageType.USER_JOINED);
        message.setMessage(message.getUserName() + " poker odasına katıldı");

        // Aktif oturumu kontrol et ve kullanıcıya gönder
        try {
            String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");
            if (userEmail != null) {
                PokerSessionDto activeSession = pokerService.getActiveSession(teamId, userEmail);
                if (activeSession != null) {
                    PokerMessageDto sessionMessage = new PokerMessageDto(
                            PokerMessageDto.MessageType.SESSION_UPDATED,
                            activeSession.getId(),
                            activeSession
                    );
                    // Sadece bu kullanıcıya aktif oturum bilgisini gönder
                    messagingTemplate.convertAndSendToUser(
                            headerAccessor.getSessionId(),
                            "/queue/poker/session",
                            sessionMessage
                    );
                }
            }
        } catch (Exception e) {
            // Hata durumunda log yazdır ama kullanıcı katılımını engelleme
            System.err.println("Error getting active session for user: " + e.getMessage());
        }

        return message;
    }

    @MessageMapping("/poker/team/{teamId}/leave")
    @SendTo("/topic/poker/team/{teamId}")
    public PokerMessageDto leavePokerRoom(@DestinationVariable Long teamId,
                                         @Payload PokerMessageDto message,
                                         SimpMessageHeaderAccessor headerAccessor) {

        // Session'dan kullanıcı bilgisini temizle
        headerAccessor.getSessionAttributes().remove("teamId");
        headerAccessor.getSessionAttributes().remove("userId");
        headerAccessor.getSessionAttributes().remove("userName");

        message.setType(PokerMessageDto.MessageType.USER_LEFT);
        message.setMessage(message.getUserName() + " poker odasından ayrıldı");

        return message;
    }

    @MessageMapping("/poker/team/{teamId}/vote")
    public void handleVote(@DestinationVariable Long teamId,
                          @Payload PokerVoteRequest voteRequest,
                          SimpMessageHeaderAccessor headerAccessor) {

        try {
            // HeaderAccessor'dan kullanıcı bilgisini al
            String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");

            if (userEmail != null) {
                // PokerService üzerinden oy kullan - bu zaten WebSocket bildirimi gönderiyor
                pokerService.castVote(voteRequest, userEmail);
            } else {
                // Hata mesajı gönder
                PokerMessageDto errorMessage = new PokerMessageDto(
                        PokerMessageDto.MessageType.ERROR,
                        voteRequest.getSessionId(),
                        "Kullanıcı oturumu bulunamadı"
                );
                messagingTemplate.convertAndSendToUser(
                        headerAccessor.getSessionId(),
                        "/queue/poker/error",
                        errorMessage
                );
            }
        } catch (Exception e) {
            // Hata durumunda tüm takıma hata mesajı gönder
            PokerMessageDto errorMessage = new PokerMessageDto(
                    PokerMessageDto.MessageType.ERROR,
                    voteRequest.getSessionId(),
                    e.getMessage()
            );
            messagingTemplate.convertAndSend("/topic/poker/team/" + teamId, errorMessage);
        }
    }

    @MessageMapping("/poker/team/{teamId}/reveal")
    public void revealVotes(@DestinationVariable Long teamId,
                           @Payload PokerMessageDto message,
                           SimpMessageHeaderAccessor headerAccessor) {

        try {
            String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");

            if (userEmail != null && message.getSessionId() != null) {
                // PokerService üzerinden oyları aç - bu zaten WebSocket bildirimi gönderiyor
                pokerService.revealVotes(message.getSessionId(), userEmail);
            } else {
                PokerMessageDto errorMessage = new PokerMessageDto(
                        PokerMessageDto.MessageType.ERROR,
                        message.getSessionId(),
                        "Kullanıcı oturumu veya oturum ID'si bulunamadı"
                );
                messagingTemplate.convertAndSendToUser(
                        headerAccessor.getSessionId(),
                        "/queue/poker/error",
                        errorMessage
                );
            }
        } catch (Exception e) {
            PokerMessageDto errorMessage = new PokerMessageDto(
                    PokerMessageDto.MessageType.ERROR,
                    message.getSessionId(),
                    e.getMessage()
            );
            messagingTemplate.convertAndSend("/topic/poker/team/" + teamId, errorMessage);
        }
    }

    @MessageMapping("/poker/team/{teamId}/start-voting")
    public void startVoting(@DestinationVariable Long teamId,
                           @Payload PokerMessageDto message,
                           SimpMessageHeaderAccessor headerAccessor) {

        try {
            String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");

            if (userEmail != null && message.getSessionId() != null) {
                // PokerService üzerinden oylamayı başlat - bu zaten WebSocket bildirimi gönderiyor
                pokerService.startVoting(message.getSessionId(), userEmail);
            } else {
                PokerMessageDto errorMessage = new PokerMessageDto(
                        PokerMessageDto.MessageType.ERROR,
                        message.getSessionId(),
                        "Kullanıcı oturumu veya oturum ID'si bulunamadı"
                );
                messagingTemplate.convertAndSendToUser(
                        headerAccessor.getSessionId(),
                        "/queue/poker/error",
                        errorMessage
                );
            }
        } catch (Exception e) {
            PokerMessageDto errorMessage = new PokerMessageDto(
                    PokerMessageDto.MessageType.ERROR,
                    message.getSessionId(),
                    e.getMessage()
            );
            messagingTemplate.convertAndSend("/topic/poker/team/" + teamId, errorMessage);
        }
    }

    @MessageMapping("/poker/team/{teamId}/complete")
    public void completeSession(@DestinationVariable Long teamId,
                               @Payload PokerMessageDto message,
                               SimpMessageHeaderAccessor headerAccessor) {

        try {
            String userEmail = (String) headerAccessor.getSessionAttributes().get("userEmail");
            String finalEstimate = message.getData() != null ? message.getData().toString() : "";

            if (userEmail != null && message.getSessionId() != null) {
                // PokerService üzerinden oturumu tamamla - bu zaten WebSocket bildirimi gönderiyor
                pokerService.completeSession(message.getSessionId(), finalEstimate, userEmail);
            } else {
                PokerMessageDto errorMessage = new PokerMessageDto(
                        PokerMessageDto.MessageType.ERROR,
                        message.getSessionId(),
                        "Kullanıcı oturumu veya oturum ID'si bulunamadı"
                );
                messagingTemplate.convertAndSendToUser(
                        headerAccessor.getSessionId(),
                        "/queue/poker/error",
                        errorMessage
                );
            }
        } catch (Exception e) {
            PokerMessageDto errorMessage = new PokerMessageDto(
                    PokerMessageDto.MessageType.ERROR,
                    message.getSessionId(),
                    e.getMessage()
            );
            messagingTemplate.convertAndSend("/topic/poker/team/" + teamId, errorMessage);
        }
    }
}
