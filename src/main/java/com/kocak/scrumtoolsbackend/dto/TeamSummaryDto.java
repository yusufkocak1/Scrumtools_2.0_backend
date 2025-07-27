package com.kocak.scrumtoolsbackend.dto;

import com.kocak.scrumtoolsbackend.entity.Team;

import java.time.LocalDateTime;

public class TeamSummaryDto {
    private String id;
    private String name;
    private String description;
    private int memberCount;
    private LocalDateTime createdAt;

    // Constructors
    public TeamSummaryDto() {}

    public TeamSummaryDto(Team team) {
        this.id = team.getId().toString();
        this.name = team.getName();
        this.description = team.getDescription();
        this.memberCount = team.getMemberCount();
        this.createdAt = team.getCreatedAt();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
