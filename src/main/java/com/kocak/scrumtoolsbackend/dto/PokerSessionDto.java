package com.kocak.scrumtoolsbackend.dto;

import com.kocak.scrumtoolsbackend.entity.PokerSession;
import java.time.LocalDateTime;
import java.util.List;

public class PokerSessionDto {
    private Long id;
    private Long teamId;
    private String teamName;
    private String storyTitle;
    private String storyDescription;
    private PokerSession.SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String finalEstimate;
    private UserDto createdBy;
    private List<PokerVoteDto> votes;
    private int totalVotes;
    private boolean hasUserVoted;

    // Constructors
    public PokerSessionDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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

    public PokerSession.SessionStatus getStatus() {
        return status;
    }

    public void setStatus(PokerSession.SessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getFinalEstimate() {
        return finalEstimate;
    }

    public void setFinalEstimate(String finalEstimate) {
        this.finalEstimate = finalEstimate;
    }

    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    public List<PokerVoteDto> getVotes() {
        return votes;
    }

    public void setVotes(List<PokerVoteDto> votes) {
        this.votes = votes;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public boolean isHasUserVoted() {
        return hasUserVoted;
    }

    public void setHasUserVoted(boolean hasUserVoted) {
        this.hasUserVoted = hasUserVoted;
    }
}
