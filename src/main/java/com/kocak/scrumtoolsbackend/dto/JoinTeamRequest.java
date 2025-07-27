package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinTeamRequest {

    @NotBlank(message = "Takım kodu boş olamaz")
    private String teamCode;

    public JoinTeamRequest() {}

    public JoinTeamRequest(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }
}
