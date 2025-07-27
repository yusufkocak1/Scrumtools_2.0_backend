package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.PokerSession;
import com.kocak.scrumtoolsbackend.entity.PokerVote;
import com.kocak.scrumtoolsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokerVoteRepository extends JpaRepository<PokerVote, Long> {

    List<PokerVote> findByPokerSessionOrderByVotedAtAsc(PokerSession pokerSession);

    Optional<PokerVote> findByPokerSessionAndUser(PokerSession pokerSession, User user);

    @Query("SELECT COUNT(pv) FROM PokerVote pv WHERE pv.pokerSession = :session")
    long countVotesBySession(@Param("session") PokerSession session);

    @Query("SELECT pv FROM PokerVote pv WHERE pv.pokerSession.id = :sessionId")
    List<PokerVote> findBySessionId(@Param("sessionId") Long sessionId);

    void deleteByPokerSessionAndUser(PokerSession pokerSession, User user);
}
