package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ApproveMemberRequest {
    @NotNull(message = "Üye ID'si boş olamaz")
    private Long memberId;

    private String role; // Admin tarafından belirlenen roller

    // Constructors
    public ApproveMemberRequest() {}
    public ApproveMemberRequest(Long memberId, String role) {
        this.memberId = memberId;
        this.role = role;
    }

    // Getters and Setters
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
