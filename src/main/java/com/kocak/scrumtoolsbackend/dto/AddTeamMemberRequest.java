package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AddTeamMemberRequest {

    @NotBlank(message = "E-posta adresi boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Rol boş olamaz")
    private String role;

    // Constructors
    public AddTeamMemberRequest() {}

    public AddTeamMemberRequest(String email, String role) {
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
