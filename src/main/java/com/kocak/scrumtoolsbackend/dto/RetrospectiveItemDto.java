package com.kocak.scrumtoolsbackend.dto;

import java.time.LocalDateTime;

public class RetrospectiveItemDto {

    private String id;
    private String content;
    private String category;
    private String type;
    private Integer votes;
    private String authorId;
    private String createdAt;

    // Constructors
    public RetrospectiveItemDto() {}

    public RetrospectiveItemDto(String id, String content, String category, String type,
                               Integer votes, String authorId, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.category = category;
        this.type = type;
        this.votes = votes;
        this.authorId = authorId;
        this.createdAt = createdAt != null ? createdAt.toString() : null;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
