package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.ActionItem;
import com.kocak.scrumtoolsbackend.entity.RetrospectiveSession;
import com.kocak.scrumtoolsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionItemRepository extends JpaRepository<ActionItem, Long> {

    List<ActionItem> findBySession(RetrospectiveSession session);

    List<ActionItem> findByAssignee(User assignee);

    List<ActionItem> findBySessionAndStatus(RetrospectiveSession session, ActionItem.ActionStatus status);

    @Query("SELECT ai FROM ActionItem ai WHERE ai.assignee = :user AND ai.status = :status ORDER BY ai.priority DESC, ai.createdAt ASC")
    List<ActionItem> findByAssigneeAndStatusOrderByPriorityDescCreatedAtAsc(@Param("user") User user, @Param("status") ActionItem.ActionStatus status);

    @Query("SELECT ai FROM ActionItem ai WHERE ai.session = :session ORDER BY ai.priority DESC, ai.createdAt ASC")
    List<ActionItem> findBySessionOrderByPriorityDescCreatedAtAsc(@Param("session") RetrospectiveSession session);
}
