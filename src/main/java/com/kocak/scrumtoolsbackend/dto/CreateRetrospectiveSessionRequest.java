package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRetrospectiveSessionRequest {

    @NotBlank(message = "Takım ID boş olamaz")
    private String teamId;

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 200, message = "Başlık en fazla 200 karakter olabilir")
    private String title;

    @NotNull(message = "Sprint numarası boş olamaz")
    private Integer sprintNumber;

    @NotBlank(message = "Template boş olamaz")
    @Size(max = 100, message = "Template en fazla 100 karakter olabilir")
    private String template;

    // Constructors
    public CreateRetrospectiveSessionRequest() {}

    public CreateRetrospectiveSessionRequest(String teamId, String title, Integer sprintNumber, String template) {
        this.teamId = teamId;
        this.title = title;
        this.sprintNumber = sprintNumber;
        this.template = template;
    }

    // Getters and Setters
    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
