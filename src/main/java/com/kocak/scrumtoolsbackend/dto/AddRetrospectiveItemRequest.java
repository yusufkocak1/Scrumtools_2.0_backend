package com.kocak.scrumtoolsbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddRetrospectiveItemRequest {

    @NotBlank(message = "Kategori boş olamaz")
    @Size(max = 100, message = "Kategori en fazla 100 karakter olabilir")
    private String category;

    @NotBlank(message = "İçerik boş olamaz")
    @Size(max = 1000, message = "İçerik en fazla 1000 karakter olabilir")
    private String content;

    @NotBlank(message = "Tip boş olamaz")
    private String type; // positive, negative, action

    // Constructors
    public AddRetrospectiveItemRequest() {}

    public AddRetrospectiveItemRequest(String category, String content, String type) {
        this.category = category;
        this.content = content;
        this.type = type;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
