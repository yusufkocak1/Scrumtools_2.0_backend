package com.kocak.scrumtoolsbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Takım adı boş olamaz")
    @Size(max = 100, message = "Takım adı en fazla 100 karakter olabilir")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeamMember> teamMembers = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (inviteCode == null) {
            generateInviteCode();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Team() {}

    public Team(String name, String description, User createdBy) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        generateInviteCode();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Set<TeamMember> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(Set<TeamMember> teamMembers) { this.teamMembers = teamMembers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public void addMember(TeamMember member) {
        teamMembers.add(member);
        member.setTeam(this);
    }

    public void removeMember(TeamMember member) {
        teamMembers.remove(member);
        member.setTeam(null);
    }

    public int getMemberCount() {
        return teamMembers.size();
    }

    public void generateInviteCode() {
        this.inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
