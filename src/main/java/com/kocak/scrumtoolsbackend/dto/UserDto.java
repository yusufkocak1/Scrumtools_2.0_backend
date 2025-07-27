package com.kocak.scrumtoolsbackend.dto;

import com.kocak.scrumtoolsbackend.entity.User;
import com.kocak.scrumtoolsbackend.entity.UserPreference;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class UserDto {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, String> preferences = new HashMap<>();

    public UserDto() {}

    public UserDto(User user) {
        this.id = user.getId().toString();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();

        // User preferences'ları Map'e dönüştür
        if (user.getPreferences() != null) {
            for (UserPreference preference : user.getPreferences()) {
                this.preferences.put(preference.getKey(), preference.getValue());
            }
        }
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Map<String, String> getPreferences() { return preferences; }
    public void setPreferences(Map<String, String> preferences) { this.preferences = preferences; }
}
