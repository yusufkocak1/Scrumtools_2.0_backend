package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.RetrospectiveItem;
import com.kocak.scrumtoolsbackend.entity.RetrospectiveVote;
import com.kocak.scrumtoolsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RetrospectiveVoteRepository extends JpaRepository<RetrospectiveVote, Long> {

    Optional<RetrospectiveVote> findByItemAndUser(RetrospectiveItem item, User user);

    boolean existsByItemAndUser(RetrospectiveItem item, User user);

    int countByItem(RetrospectiveItem item);
}
