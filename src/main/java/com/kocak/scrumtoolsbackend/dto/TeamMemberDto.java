package com.kocak.scrumtoolsbackend.dto;

import com.kocak.scrumtoolsbackend.entity.TeamMember;

import java.util.List;
import java.util.stream.Collectors;

public class TeamMemberDto {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles; // Birden fazla rol için değiştirildi
    private String role; // Backward compatibility için korundu

    // Constructors
    public TeamMemberDto() {}

    public TeamMemberDto(TeamMember teamMember) {
        this.id = teamMember.getUser().getId().toString();
        this.email = teamMember.getUser().getEmail();
        this.firstName = teamMember.getUser().getFirstName();
        this.lastName = teamMember.getUser().getLastName();

        // Tüm rolleri string listesine çevir
        this.roles = teamMember.getRoles().stream()
            .map(Enum::name)
            .collect(Collectors.toList());

        // Backward compatibility için ilk rolü al
        this.role = teamMember.getRole().name();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    // Backward compatibility için korundu
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
