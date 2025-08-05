package com.kocak.scrumtoolsbackend.repository.projection;

/**
 * Memory-efficient projection for User basic info
 * Sadece gerekli alanları yükler, ilişkili entity'leri yüklemez
 */
public interface UserBasicProjection {
    Long getId();
    String getUsername();
    String getEmail();
    String getFirstName();
    String getLastName();
}

/**
 * Poker session için minimal projection
 */
public interface PokerSessionBasicProjection {
    Long getId();
    String getSessionName();
    String getStatus();
    Long getTeamId();
}

/**
 * Team member count projection
 */
public interface TeamMemberCountProjection {
    Long getTeamId();
    Long getMemberCount();
}
