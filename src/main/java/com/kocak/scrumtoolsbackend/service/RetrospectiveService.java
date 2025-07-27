package com.kocak.scrumtoolsbackend.service;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.entity.*;
import com.kocak.scrumtoolsbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RetrospectiveService {

    @Autowired
    private RetrospectiveSessionRepository sessionRepository;

    @Autowired
    private RetrospectiveItemRepository itemRepository;

    @Autowired
    private RetrospectiveVoteRepository voteRepository;

    @Autowired
    private ActionItemRepository actionItemRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    public RetrospectiveSessionDto createRetrospectiveSession(CreateRetrospectiveSessionRequest request, User currentUser) {
        Long teamId = Long.parseLong(request.getTeamId());
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

        RetrospectiveSession session = new RetrospectiveSession(
            request.getTitle(),
            team,
            request.getSprintNumber(),
            request.getTemplate(),
            currentUser
        );

        session = sessionRepository.save(session);

        return new RetrospectiveSessionDto(
            session.getId().toString(),
            session.getTitle(),
            session.getTeam().getId().toString(),
            session.getSprintNumber(),
            session.getTemplate(),
            session.getStatus().toString().toLowerCase(),
            session.getCreatedAt()
        );
    }

    public RetrospectiveItemDto addRetrospectiveItem(Long sessionId, AddRetrospectiveItemRequest request, User currentUser) {
        RetrospectiveSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Retrospektif oturumu bulunamadı"));

        RetrospectiveItem.ItemType itemType;
        try {
            itemType = RetrospectiveItem.ItemType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Geçersiz item tipi: " + request.getType());
        }

        RetrospectiveItem item = new RetrospectiveItem(
            request.getContent(),
            request.getCategory(),
            itemType,
            session,
            currentUser
        );

        item = itemRepository.save(item);

        return new RetrospectiveItemDto(
            item.getId().toString(),
            item.getContent(),
            item.getCategory(),
            item.getType().toString().toLowerCase(),
            item.getVotes(),
            item.getAuthor().getId().toString(),
            item.getCreatedAt()
        );
    }

    public Integer voteRetrospectiveItem(Long itemId, User currentUser) {
        RetrospectiveItem item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Retrospektif item bulunamadı"));

        // Kullanıcının daha önce oy verip vermediğini kontrol et
        if (voteRepository.existsByItemAndUser(item, currentUser)) {
            throw new RuntimeException("Bu item için zaten oy verdiniz");
        }

        // Yeni oy oluştur
        RetrospectiveVote vote = new RetrospectiveVote(item, currentUser);
        voteRepository.save(vote);

        // Item'in toplam oy sayısını güncelle
        int totalVotes = voteRepository.countByItem(item);
        item.setVotes(totalVotes);
        itemRepository.save(item);

        return totalVotes;
    }

    public ActionItemDto createActionItem(Long sessionId, CreateActionItemRequest request) {
        RetrospectiveSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Retrospektif oturumu bulunamadı"));

        User assignee = userRepository.findById(request.getAssigneeId())
            .orElseThrow(() -> new RuntimeException("Atanan kullanıcı bulunamadı"));

        ActionItem.Priority priority;
        try {
            priority = ActionItem.Priority.valueOf(request.getPriority().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Geçersiz öncelik: " + request.getPriority());
        }

        ActionItem actionItem = new ActionItem(
            request.getTitle(),
            request.getDescription(),
            session,
            assignee,
            priority
        );

        actionItem = actionItemRepository.save(actionItem);

        return new ActionItemDto(
            actionItem.getId().toString(),
            actionItem.getTitle(),
            actionItem.getDescription(),
            assignee.getFirstName() + " " + assignee.getLastName(),
            actionItem.getPriority().toString().toLowerCase(),
            actionItem.getStatus().toString().toLowerCase(),
            actionItem.getCreatedAt()
        );
    }

    public RetrospectiveResultsDto getRetrospectiveResults(Long sessionId) {
        RetrospectiveSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Retrospektif oturumu bulunamadı"));

        // Items'ları oy sayısına göre sırala
        List<RetrospectiveItem> items = itemRepository.findBySessionOrderByVotesDesc(session);
        List<RetrospectiveItemDto> itemDtos = items.stream()
            .map(item -> new RetrospectiveItemDto(
                item.getId().toString(),
                item.getContent(),
                item.getCategory(),
                item.getType().toString().toLowerCase(),
                item.getVotes(),
                item.getAuthor().getId().toString(),
                item.getCreatedAt()
            ))
            .collect(Collectors.toList());

        // Action items'ları önceliğe göre sırala
        List<ActionItem> actionItems = actionItemRepository.findBySessionOrderByPriorityDescCreatedAtAsc(session);
        List<ActionItemDto> actionItemDtos = actionItems.stream()
            .map(actionItem -> new ActionItemDto(
                actionItem.getId().toString(),
                actionItem.getTitle(),
                actionItem.getDescription(),
                actionItem.getAssignee().getFirstName() + " " + actionItem.getAssignee().getLastName(),
                actionItem.getPriority().toString().toLowerCase(),
                actionItem.getStatus().toString().toLowerCase(),
                actionItem.getCreatedAt()
            ))
            .collect(Collectors.toList());

        return new RetrospectiveResultsDto(itemDtos, actionItemDtos);
    }
}
