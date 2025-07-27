package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateActionItemRequest {

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 200, message = "Başlık en fazla 200 karakter olabilir")
    private String title;

    @Size(max = 1000, message = "Açıklama en fazla 1000 karakter olabilir")
    private String description;

    @NotNull(message = "Atanan kişi ID boş olamaz")
    private Long assigneeId;

    @NotBlank(message = "Öncelik boş olamaz")
    private String priority; // high, medium, low

    // Constructors
    public CreateActionItemRequest() {}

    public CreateActionItemRequest(String title, String description, Long assigneeId, String priority) {
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.priority = priority;
    }

    // Getters and Setters
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

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
