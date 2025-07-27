package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePokerSessionRequest {

    @NotNull(message = "Takım ID boş olamaz")
    private Long teamId;

    @NotBlank(message = "Story başlığı boş olamaz")
    private String storyTitle;

    private String storyDescription;

    // Constructors
    public CreatePokerSessionRequest() {}

    public CreatePokerSessionRequest(Long teamId, String storyTitle, String storyDescription) {
        this.teamId = teamId;
        this.storyTitle = storyTitle;
        this.storyDescription = storyDescription;
    }

    // Getters and Setters
    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public String getStoryDescription() {
        return storyDescription;
    }

    public void setStoryDescription(String storyDescription) {
        this.storyDescription = storyDescription;
    }
}
