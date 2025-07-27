package com.kocak.scrumtoolsbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poker_sessions")
public class PokerSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "story_title", nullable = false)
    private String storyTitle;

    @Column(name = "story_description")
    private String storyDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.WAITING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "final_estimate")
    private String finalEstimate;

    @OneToMany(mappedBy = "pokerSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PokerVote> votes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public enum SessionStatus {
        WAITING,    // Oyuncular katılıyor
        VOTING,     // Oylama devam ediyor
        REVEALED,   // Oylar gösterildi
        COMPLETED   // Oturum tamamlandı
    }

    // Constructors
    public PokerSession() {}

    public PokerSession(Team team, String storyTitle, String storyDescription, User createdBy) {
        this.team = team;
        this.storyTitle = storyTitle;
        this.storyDescription = storyDescription;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
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

    public List<PokerVote> getVotes() {
        return votes;
    }

    public void setVotes(List<PokerVote> votes) {
        this.votes = votes;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
