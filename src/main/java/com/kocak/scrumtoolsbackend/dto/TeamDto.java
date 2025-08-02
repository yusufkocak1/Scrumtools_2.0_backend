package com.kocak.scrumtoolsbackend.dto;

import com.kocak.scrumtoolsbackend.entity.Team;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TeamDto {
    private String id;
    private String name;
    private String description;
    private String createdBy;
    private String inviteCode; // Davet kodu eklendi
    private List<TeamMemberDto> members;
    private LocalDateTime createdAt;

    // Constructors
    public TeamDto() {}

    public TeamDto(Team team) {
        this.id = team.getId().toString();
        this.name = team.getName();
        this.description = team.getDescription();
        this.createdBy = team.getCreatedBy().getEmail();
        this.inviteCode = team.getInviteCode(); // Davet kodu eklendi
        this.members = team.getTeamMembers().stream()
                .map(TeamMemberDto::new)
                .collect(Collectors.toList());
        this.createdAt = team.getCreatedAt();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public List<TeamMemberDto> getMembers() { return members; }
    public void setMembers(List<TeamMemberDto> members) { this.members = members; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
