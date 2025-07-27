package com.kocak.scrumtoolsbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "team_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "user_id"}))
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection(targetClass = TeamRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "team_member_roles",
                    joinColumns = @JoinColumn(name = "team_member_id"))
    @Column(name = "role")
    private Set<TeamRole> roles = new HashSet<>();

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        // Eğer hiç rol atanmamışsa, default olarak MEMBER rolü ekle
        if (roles.isEmpty()) {
            roles.add(TeamRole.MEMBER);
        }
    }

    // Constructors
    public TeamMember() {
        this.roles = new HashSet<>();
    }

    public TeamMember(Team team, User user) {
        this.team = team;
        this.user = user;
        this.roles = new HashSet<>();
        this.roles.add(TeamRole.MEMBER); // Default rol
    }

    public TeamMember(Team team, User user, TeamRole role) {
        this.team = team;
        this.user = user;
        this.roles = new HashSet<>();
        this.roles.add(role);
    }

    public TeamMember(Team team, User user, Set<TeamRole> roles) {
        this.team = team;
        this.user = user;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        // En az MEMBER rolü olmalı
        if (this.roles.isEmpty()) {
            this.roles.add(TeamRole.MEMBER);
        }
    }

    // Rol yönetimi metodları
    public void addRole(TeamRole role) {
        this.roles.add(role);
    }

    public void removeRole(TeamRole role) {
        this.roles.remove(role);
        // En az MEMBER rolü kalmalı
        if (this.roles.isEmpty()) {
            this.roles.add(TeamRole.MEMBER);
        }
    }

    public boolean hasRole(TeamRole role) {
        return this.roles.contains(role);
    }

    public boolean isAdmin() {
        return this.roles.contains(TeamRole.ADMIN);
    }

    public boolean isScrumMaster() {
        return this.roles.contains(TeamRole.SCRUM_MASTER);
    }

    public boolean isProductOwner() {
        return this.roles.contains(TeamRole.PRODUCT_OWNER);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Set<TeamRole> getRoles() { return roles; }
    public void setRoles(Set<TeamRole> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
        // En az MEMBER rolü olmalı
        if (this.roles.isEmpty()) {
            this.roles.add(TeamRole.MEMBER);
        }
    }

    // Backward compatibility için - sadece ilk rolü döndürür
    @Deprecated
    public TeamRole getRole() {
        return roles.isEmpty() ? TeamRole.MEMBER : roles.iterator().next();
    }

    // Backward compatibility için - role set'e ekler
    @Deprecated
    public void setRole(TeamRole role) {
        this.roles.clear();
        this.roles.add(role != null ? role : TeamRole.MEMBER);
    }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public enum TeamRole {
        ADMIN, SCRUM_MASTER, PRODUCT_OWNER, DEVELOPER, TESTER, ANALYST, MEMBER, TECHNICAL_LEAD, OBSERVER
    }
}
