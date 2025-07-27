package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateTeamRequest {

    @NotBlank(message = "Takım adı boş olamaz")
    @Size(max = 100, message = "Takım adı en fazla 100 karakter olabilir")
    private String name;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;

    private List<String> members;

    // Constructors
    public CreateTeamRequest() {}

    public CreateTeamRequest(String name, String description, List<String> members) {
        this.name = name;
        this.description = description;
        this.members = members;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
}
