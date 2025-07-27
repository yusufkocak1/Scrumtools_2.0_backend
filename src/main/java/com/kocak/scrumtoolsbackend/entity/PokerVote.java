package com.kocak.scrumtoolsbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "poker_votes")
public class PokerVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poker_session_id", nullable = false)
    private PokerSession pokerSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "vote_value", nullable = false)
    private String voteValue;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt = LocalDateTime.now();

    @Column(name = "is_revealed", nullable = false)
    private boolean isRevealed = false;

    // Constructors
    public PokerVote() {}

    public PokerVote(PokerSession pokerSession, User user, String voteValue) {
        this.pokerSession = pokerSession;
        this.user = user;
        this.voteValue = voteValue;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PokerSession getPokerSession() {
        return pokerSession;
    }

    public void setPokerSession(PokerSession pokerSession) {
        this.pokerSession = pokerSession;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getVoteValue() {
        return voteValue;
    }

    public void setVoteValue(String voteValue) {
        this.voteValue = voteValue;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }
}
