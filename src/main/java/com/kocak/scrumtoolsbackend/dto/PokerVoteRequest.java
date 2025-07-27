package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PokerVoteRequest {

    @NotNull(message = "Oturum ID boş olamaz")
    private Long sessionId;

    @NotBlank(message = "Oy değeri boş olamaz")
    private String voteValue;

    // Constructors
    public PokerVoteRequest() {}

    public PokerVoteRequest(Long sessionId, String voteValue) {
        this.sessionId = sessionId;
        this.voteValue = voteValue;
    }

    // Getters and Setters
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getVoteValue() {
        return voteValue;
    }

    public void setVoteValue(String voteValue) {
        this.voteValue = voteValue;
    }
}
