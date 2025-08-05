package com.kocak.scrumtoolsbackend.repository.projection;

/**
 * Memory-efficient projection for Team basic info
 * Bu projection gereksiz alanları yüklemeyerek memory kullanımını azaltır
 */
public interface TeamBasicProjection {
    Long getId();
    String getName();
    String getDescription();
    String getInviteCode();
}
