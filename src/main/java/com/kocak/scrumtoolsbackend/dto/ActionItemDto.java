package com.kocak.scrumtoolsbackend.dto;

import java.time.LocalDateTime;

public class ActionItemDto {

    private String id;
    private String title;
    private String description;
    private String assignee;
    private String priority;
    private String status;
    private String createdAt;

    // Constructors
    public ActionItemDto() {}

    public ActionItemDto(String id, String title, String description, String assignee,
                        String priority, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.priority = priority;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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
