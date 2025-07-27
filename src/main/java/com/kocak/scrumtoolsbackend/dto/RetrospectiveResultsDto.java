package com.kocak.scrumtoolsbackend.dto;

import java.util.List;

public class RetrospectiveResultsDto {

    private List<RetrospectiveItemDto> items;
    private List<ActionItemDto> actionItems;

    // Constructors
    public RetrospectiveResultsDto() {}

    public RetrospectiveResultsDto(List<RetrospectiveItemDto> items, List<ActionItemDto> actionItems) {
        this.items = items;
        this.actionItems = actionItems;
    }

    // Getters and Setters
    public List<RetrospectiveItemDto> getItems() {
        return items;
    }

    public void setItems(List<RetrospectiveItemDto> items) {
        this.items = items;
    }

    public List<ActionItemDto> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItemDto> actionItems) {
        this.actionItems = actionItems;
    }
}
