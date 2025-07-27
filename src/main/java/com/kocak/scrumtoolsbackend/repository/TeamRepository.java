package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.Team;
import com.kocak.scrumtoolsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByCreatedBy(User createdBy);

    @Query("SELECT t FROM Team t JOIN t.teamMembers tm WHERE tm.user = :user")
    List<Team> findTeamsByMember(@Param("user") User user);

    @Query("SELECT t FROM Team t WHERE t.createdBy = :user OR EXISTS (SELECT tm FROM TeamMember tm WHERE tm.team = t AND tm.user = :user)")
    List<Team> findAllUserTeams(@Param("user") User user);
}
