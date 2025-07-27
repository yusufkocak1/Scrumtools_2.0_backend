package com.kocak.scrumtoolsbackend.dto;

import java.time.LocalDateTime;

public class RetrospectiveSessionDto {

    private String id;
    private String title;
    private String teamId;
    private Integer sprintNumber;
    private String template;
    private String status;
    private String createdAt;

    // Constructors
    public RetrospectiveSessionDto() {}

    public RetrospectiveSessionDto(String id, String title, String teamId, Integer sprintNumber,
                                 String template, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.teamId = teamId;
        this.sprintNumber = sprintNumber;
        this.template = template;
        this.status = status;
        this.createdAt = createdAt != null ? createdAt.toString() : null;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Integer getSprintNumber() {
        return sprintNumber;
    }

    public void setSprintNumber(Integer sprintNumber) {
        this.sprintNumber = sprintNumber;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
