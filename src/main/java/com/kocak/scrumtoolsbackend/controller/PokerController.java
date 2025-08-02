package com.kocak.scrumtoolsbackend.controller;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.service.PokerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/poker")
@CrossOrigin(origins = "*")
public class PokerController {

    @Autowired
    private PokerService pokerService;

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<PokerSessionDto>> createSession(
            @Valid @RequestBody CreatePokerSessionRequest request,
            Authentication authentication) {
        try {
            PokerSessionDto session = pokerService.createSession(request, authentication.getName());
            ApiResponse<PokerSessionDto> response = ApiResponse.success(session);
            response.setMessage("Poker oturumu başarıyla oluşturuldu");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/sessions/{sessionId}/join")
    public ResponseEntity<ApiResponse<PokerSessionDto>> joinSession(
            @PathVariable Long sessionId,
            Authentication authentication) {
        try {
            PokerSessionDto session = pokerService.joinSession(sessionId, authentication.getName());
            ApiResponse<PokerSessionDto> response = ApiResponse.success(session);
            response.setMessage("Poker oturumuna başarıyla katıldınız");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/votes")
    public ResponseEntity<ApiResponse<PokerVoteDto>> castVote(
            @Valid @RequestBody PokerVoteRequest request,
            Authentication authentication) {
        try {
            PokerVoteDto vote = pokerService.castVote(request, authentication.getName());
            ApiResponse<PokerVoteDto> response = ApiResponse.success(vote);
            response.setMessage("Oyunuz başarıyla kaydedildi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/sessions/{sessionId}/reveal")
    public ResponseEntity<ApiResponse<PokerSessionDto>> revealVotes(
            @PathVariable Long sessionId,
            Authentication authentication) {
        try {
            PokerSessionDto session = pokerService.revealVotes(sessionId, authentication.getName());
            ApiResponse<PokerSessionDto> response = ApiResponse.success(session);
            response.setMessage("Oylar başarıyla açıldı");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<ApiResponse<PokerSessionDto>> completeSession(
            @PathVariable Long sessionId,
            @RequestParam String finalEstimate,
            Authentication authentication) {
        try {
            PokerSessionDto session = pokerService.completeSession(sessionId, finalEstimate, authentication.getName());
            ApiResponse<PokerSessionDto> response = ApiResponse.success(session);
            response.setMessage("Poker oturumu başarıyla tamamlandı");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/sessions/{sessionId}/start-voting")
    public ResponseEntity<ApiResponse<PokerSessionDto>> startVoting(
            @PathVariable Long sessionId,
            Authentication authentication) {
        try {
            PokerSessionDto session = pokerService.startVoting(sessionId, authentication.getName());
            ApiResponse<PokerSessionDto> response = ApiResponse.success(session);
            response.setMessage("Oylama başarıyla başlatıldı");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/teams/{teamId}/active-session")
    public ResponseEntity<ApiResponse<PokerSessionDto>> getActiveSession(
            @PathVariable Long teamId,
            Authentication authentication) {
        try {
            PokerSessionDto session = pokerService.getActiveSession(teamId, authentication.getName());
            if (session == null) {
                ApiResponse<PokerSessionDto> response = ApiResponse.success((PokerSessionDto) null);
                response.setMessage("Aktif poker oturumu bulunamadı");
                return ResponseEntity.ok(response);
            }
            ApiResponse<PokerSessionDto> response = ApiResponse.success(session);
            response.setMessage("Aktif poker oturumu bulundu");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getActiveSession: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/teams/{teamId}/sessions")
    public ResponseEntity<ApiResponse<List<PokerSessionDto>>> getTeamSessions(
            @PathVariable Long teamId,
            Authentication authentication) {
        try {
            List<PokerSessionDto> sessions = pokerService.getTeamSessions(teamId, authentication.getName());
            ApiResponse<List<PokerSessionDto>> response = ApiResponse.success(sessions);
            response.setMessage("Takım poker oturumları başarıyla getirildi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
