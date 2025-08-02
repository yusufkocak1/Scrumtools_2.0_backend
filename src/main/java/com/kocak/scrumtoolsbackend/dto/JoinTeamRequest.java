package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;


public class JoinTeamRequest {

    @NotBlank(message = "invite kodu boş olamaz")
    private String inviteCode;

    public JoinTeamRequest() {}

    public JoinTeamRequest(String inviteCode) {
        this.inviteCode = inviteCode;
    }
    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
