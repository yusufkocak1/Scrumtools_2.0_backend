package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.RetrospectiveItem;
import com.kocak.scrumtoolsbackend.entity.RetrospectiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetrospectiveItemRepository extends JpaRepository<RetrospectiveItem, Long> {

    List<RetrospectiveItem> findBySession(RetrospectiveSession session);

    List<RetrospectiveItem> findBySessionOrderByVotesDesc(RetrospectiveSession session);

    @Query("SELECT ri FROM RetrospectiveItem ri WHERE ri.session = :session AND ri.type = :type ORDER BY ri.votes DESC")
    List<RetrospectiveItem> findBySessionAndTypeOrderByVotesDesc(@Param("session") RetrospectiveSession session, @Param("type") RetrospectiveItem.ItemType type);

    @Query("SELECT ri FROM RetrospectiveItem ri WHERE ri.session = :session AND ri.category = :category ORDER BY ri.votes DESC")
    List<RetrospectiveItem> findBySessionAndCategoryOrderByVotesDesc(@Param("session") RetrospectiveSession session, @Param("category") String category);
}
