package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.PokerSession;
import com.kocak.scrumtoolsbackend.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokerSessionRepository extends JpaRepository<PokerSession, Long> {

    List<PokerSession> findByTeamOrderByCreatedAtDesc(Team team);

    @Query("SELECT ps FROM PokerSession ps WHERE ps.team = :team AND ps.status IN ('WAITING', 'VOTING', 'REVEALED')")
    Optional<PokerSession> findActiveSessionByTeam(@Param("team") Team team);

    @Query("SELECT ps FROM PokerSession ps WHERE ps.team.id = :teamId AND ps.status IN ('WAITING', 'VOTING', 'REVEALED')")
    Optional<PokerSession> findActiveSessionByTeamId(@Param("teamId") Long teamId);

    List<PokerSession> findByTeamAndStatus(Team team, PokerSession.SessionStatus status);
}
