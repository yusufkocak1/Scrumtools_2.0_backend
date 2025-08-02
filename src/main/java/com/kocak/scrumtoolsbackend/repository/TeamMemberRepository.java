package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.Team;
import com.kocak.scrumtoolsbackend.entity.TeamMember;
import com.kocak.scrumtoolsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeam(Team team);

    List<TeamMember> findByUser(User user);

    Optional<TeamMember> findByTeamAndUser(Team team, User user);

    boolean existsByTeamAndUser(Team team, User user);

    void deleteByTeamAndUser(Team team, User user);

    // Status ile ilgili sorgular
    List<TeamMember> findByTeamAndStatus(Team team, TeamMember.MemberStatus status);

    List<TeamMember> findByTeamAndStatusIn(Team team, List<TeamMember.MemberStatus> statuses);
}
