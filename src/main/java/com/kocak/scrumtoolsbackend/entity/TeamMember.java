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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.PENDING;

    @ElementCollection(targetClass = TeamRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "team_member_roles",
                    joinColumns = @JoinColumn(name = "team_member_id"))
    @Column(name = "role")
    private Set<TeamRole> roles = new HashSet<>();

    // Geçici olarak eski role kolonu - migration sonrası kaldırılacak
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private TeamRole legacyRole;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        // Eğer hiç rol atanmamışsa, default olarak MEMBER rolü ekle
        if (roles.isEmpty()) {
            roles.add(TeamRole.OBSERVER);
        }
        // Legacy role kolonu için de default değer ata
        if (legacyRole == null) {
            legacyRole = TeamRole.OBSERVER;
        }
        // Default olarak PENDING status
        if (status == null) {
            status = MemberStatus.PENDING;
        }
    }

    @PostLoad
    protected void onLoad() {
        // Veritabanından yüklendiğinde, legacy role'dan roles set'ini oluştur
        if (roles.isEmpty() && legacyRole != null) {
            roles.add(legacyRole);
        }
    }

    // Constructors
    public TeamMember() {
        this.roles = new HashSet<>();
        this.status = MemberStatus.PENDING;
    }

    public TeamMember(Team team, User user) {
        this.team = team;
        this.user = user;
        this.roles = new HashSet<>();
        this.roles.add(TeamRole.OBSERVER); // Default rol
        this.status = MemberStatus.PENDING; // Default olarak pending
    }

    public TeamMember(Team team, User user, TeamRole role) {
        this.team = team;
        this.user = user;
        this.roles = new HashSet<>();
        this.roles.add(role);
        this.status = MemberStatus.PENDING;
    }

    public TeamMember(Team team, User user, Set<TeamRole> roles) {
        this.team = team;
        this.user = user;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        // En az MEMBER rolü olmalı
        if (this.roles.isEmpty()) {
            this.roles.add(TeamRole.OBSERVER);
        }
        this.status = MemberStatus.PENDING;
    }

    // Status kontrol metodları
    public boolean isPending() {
        return status == MemberStatus.PENDING;
    }

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    public boolean isRejected() {
        return status == MemberStatus.REJECTED;
    }

    public void approve() {
        this.status = MemberStatus.ACTIVE;
    }

    public void reject() {
        this.status = MemberStatus.REJECTED;
    }

    // Rol yönetimi metodları
    public void addRole(TeamRole role) {
        this.roles.add(role);
    }

    public void removeRole(TeamRole role) {
        this.roles.remove(role);
        // En az MEMBER rolü kalmalı
        if (this.roles.isEmpty()) {
            this.roles.add(TeamRole.OBSERVER);
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
            this.roles.add(TeamRole.OBSERVER);
        }
    }

    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }

    // Backward compatibility için - sadece ilk rolü döndürür
    @Deprecated
    public TeamRole getRole() {
        return roles.isEmpty() ? TeamRole.OBSERVER : roles.iterator().next();
    }

    // Backward compatibility için - role set'e ekler
    @Deprecated
    public void setRole(TeamRole role) {
        this.roles.clear();
        this.roles.add(role != null ? role : TeamRole.OBSERVER);
    }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public enum TeamRole {
        ADMIN, SCRUM_MASTER, PRODUCT_OWNER, DEVELOPER, TESTER, ANALYST, TECHNICAL_LEAD, OBSERVER
    }

    public enum MemberStatus {
        PENDING,    // Onay bekliyor
        ACTIVE,     // Aktif üye
        REJECTED    // Reddedildi
    }
}
