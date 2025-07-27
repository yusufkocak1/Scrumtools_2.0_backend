package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.RetrospectiveSession;
import com.kocak.scrumtoolsbackend.entity.Team;
import com.kocak.scrumtoolsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetrospectiveSessionRepository extends JpaRepository<RetrospectiveSession, Long> {

    List<RetrospectiveSession> findByTeam(Team team);

    List<RetrospectiveSession> findByTeamAndSprintNumber(Team team, Integer sprintNumber);

    @Query("SELECT rs FROM RetrospectiveSession rs WHERE rs.team IN :teams ORDER BY rs.createdAt DESC")
    List<RetrospectiveSession> findByTeamsOrderByCreatedAtDesc(@Param("teams") List<Team> teams);

    @Query("SELECT rs FROM RetrospectiveSession rs WHERE rs.createdBy = :user ORDER BY rs.createdAt DESC")
    List<RetrospectiveSession> findByCreatedByOrderByCreatedAtDesc(@Param("user") User user);
}
