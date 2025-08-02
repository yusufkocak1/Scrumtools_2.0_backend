package com.kocak.scrumtoolsbackend.dto;

public class PokerMessageDto {
    private MessageType type;
    private Long sessionId;
    private Object data;
    private String message;
    private Long userId;
    private String userName;

    public enum MessageType {
        SESSION_CREATED,
        SESSION_UPDATED,
        VOTING_STARTED,
        VOTE_CAST,
        VOTES_REVEALED,
        SESSION_COMPLETED,
        USER_JOINED,
        USER_LEFT,
        ERROR
    }

    // Constructors
    public PokerMessageDto() {}

    public PokerMessageDto(MessageType type, Long sessionId, Object data) {
        this.type = type;
        this.sessionId = sessionId;
        this.data = data;
    }

    // Getters and Setters
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
