package com.kocak.scrumtoolsbackend.service;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.entity.*;
import com.kocak.scrumtoolsbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PokerService {

    @Autowired
    private PokerSessionRepository pokerSessionRepository;

    @Autowired
    private PokerVoteRepository pokerVoteRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public PokerSessionDto createSession(CreatePokerSessionRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

        // Kullanıcının takımda olup olmadığını kontrol et
        if (!teamMemberRepository.existsByTeamAndUser(team, user)) {
            throw new RuntimeException("Bu takımda scrum poker oturumu oluşturma yetkiniz yok");
        }

        // Aktif oturum varsa hata ver
        Optional<PokerSession> activeSession = pokerSessionRepository.findActiveSessionByTeam(team);
        if (activeSession.isPresent()) {
            throw new RuntimeException("Bu takım için zaten aktif bir poker oturumu var");
        }

        PokerSession session = new PokerSession(team, request.getStoryTitle(), request.getStoryDescription(), user);
        session = pokerSessionRepository.save(session);

        PokerSessionDto sessionDto = convertToDto(session, user);

        // WebSocket ile takıma bildir
        PokerMessageDto message = new PokerMessageDto(
                PokerMessageDto.MessageType.SESSION_CREATED,
                session.getId(),
                sessionDto
        );
        messagingTemplate.convertAndSend("/topic/poker/team/" + team.getId(), message);

        return sessionDto;
    }

    public PokerSessionDto joinSession(Long sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        PokerSession session = pokerSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Poker oturumu bulunamadı"));

        // Kullanıcının takımda olup olmadığını kontrol et
        if (!teamMemberRepository.existsByTeamAndUser(session.getTeam(), user)) {
            throw new RuntimeException("Bu poker oturumuna katılma yetkiniz yok");
        }

        PokerSessionDto sessionDto = convertToDto(session, user);

        // WebSocket ile takıma kullanıcının katıldığını bildir
        PokerMessageDto message = new PokerMessageDto(
                PokerMessageDto.MessageType.USER_JOINED,
                session.getId(),
                null
        );
        message.setUserId(user.getId());
        message.setUserName(user.getFirstName() + " " + user.getLastName());
        messagingTemplate.convertAndSend("/topic/poker/team/" + session.getTeam().getId(), message);

        return sessionDto;
    }

    public PokerVoteDto castVote(PokerVoteRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        PokerSession session = pokerSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Poker oturumu bulunamadı"));

        // Kullanıcının takımda olup olmadığını kontrol et
        if (!teamMemberRepository.existsByTeamAndUser(session.getTeam(), user)) {
            throw new RuntimeException("Bu poker oturumunda oy kullanma yetkiniz yok");
        }

        // Oturum durumunu kontrol et
        if (session.getStatus() != PokerSession.SessionStatus.WAITING &&
            session.getStatus() != PokerSession.SessionStatus.VOTING) {
            throw new RuntimeException("Bu oturumda artık oy kullanılamaz");
        }

        // Önceki oyu varsa güncelle, yoksa yeni oy oluştur
        Optional<PokerVote> existingVote = pokerVoteRepository.findByPokerSessionAndUser(session, user);
        PokerVote vote;

        if (existingVote.isPresent()) {
            vote = existingVote.get();
            vote.setVoteValue(request.getVoteValue());
            vote.setVotedAt(LocalDateTime.now());
        } else {
            vote = new PokerVote(session, user, request.getVoteValue());
            // Oturum durumunu VOTING olarak güncelle
            if (session.getStatus() == PokerSession.SessionStatus.WAITING) {
                session.setStatus(PokerSession.SessionStatus.VOTING);
                pokerSessionRepository.save(session);
            }
        }

        vote = pokerVoteRepository.save(vote);
        PokerVoteDto voteDto = convertVoteToDto(vote);

        // WebSocket ile takıma oy bilgisini gönder
        PokerMessageDto message = new PokerMessageDto(
                PokerMessageDto.MessageType.VOTE_CAST,
                session.getId(),
                voteDto
        );
        messagingTemplate.convertAndSend("/topic/poker/team/" + session.getTeam().getId(), message);

        return voteDto;
    }

    public PokerSessionDto revealVotes(Long sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        PokerSession session = pokerSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Poker oturumu bulunamadı"));

        // Sadece oturum oluşturan kişi oyları açabilir
        if (!session.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Sadece oturum oluşturan kişi oyları açabilir");
        }

        // Oturum durumunu kontrol et
        if (session.getStatus() != PokerSession.SessionStatus.VOTING) {
            throw new RuntimeException("Bu oturumda henüz oy kullanılmamış veya oylar zaten açılmış");
        }

        // Oturum durumunu güncelle
        session.setStatus(PokerSession.SessionStatus.REVEALED);
        session = pokerSessionRepository.save(session);

        // Tüm oyları açılmış olarak işaretle
        List<PokerVote> votes = pokerVoteRepository.findByPokerSessionOrderByVotedAtAsc(session);
        votes.forEach(vote -> vote.setRevealed(true));
        pokerVoteRepository.saveAll(votes);

        PokerSessionDto sessionDto = convertToDto(session, user);

        // WebSocket ile takıma oyların açıldığını bildir
        PokerMessageDto message = new PokerMessageDto(
                PokerMessageDto.MessageType.VOTES_REVEALED,
                session.getId(),
                sessionDto
        );
        messagingTemplate.convertAndSend("/topic/poker/team/" + session.getTeam().getId(), message);

        return sessionDto;
    }

    public PokerSessionDto completeSession(Long sessionId, String finalEstimate, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        PokerSession session = pokerSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Poker oturumu bulunamadı"));

        // Sadece oturum oluşturan kişi oturumu tamamlayabilir
        if (!session.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Sadece oturum oluşturan kişi oturumu tamamlayabilir");
        }

        // Oturum durumunu güncelle
        session.setStatus(PokerSession.SessionStatus.COMPLETED);
        session.setFinalEstimate(finalEstimate);
        session.setCompletedAt(LocalDateTime.now());
        session = pokerSessionRepository.save(session);

        PokerSessionDto sessionDto = convertToDto(session, user);

        // WebSocket ile takıma oturumun tamamlandığını bildir
        PokerMessageDto message = new PokerMessageDto(
                PokerMessageDto.MessageType.SESSION_COMPLETED,
                session.getId(),
                sessionDto
        );
        messagingTemplate.convertAndSend("/topic/poker/team/" + session.getTeam().getId(), message);

        return sessionDto;
    }

    public PokerSessionDto startVoting(Long sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        PokerSession session = pokerSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Poker oturumu bulunamadı"));

        // Sadece oturum oluşturan kişi oylamayı başlatabilir
        if (!session.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Sadece oturum oluşturan kişi oylamayı başlatabilir");
        }

        // Oturum durumunu kontrol et - sadece WAITING durumundayken başlatılabilir
        if (session.getStatus() != PokerSession.SessionStatus.WAITING) {
            throw new RuntimeException("Oylama sadece beklemede olan oturumlarda başlatılabilir");
        }

        // Oturum durumunu VOTING olarak güncelle
        session.setStatus(PokerSession.SessionStatus.VOTING);
        session = pokerSessionRepository.save(session);

        PokerSessionDto sessionDto = convertToDto(session, user);

        // WebSocket ile takıma oylamanın başladığını bildir
        PokerMessageDto message = new PokerMessageDto(
                PokerMessageDto.MessageType.VOTING_STARTED,
                session.getId(),
                sessionDto
        );
        messagingTemplate.convertAndSend("/topic/poker/team/" + session.getTeam().getId(), message);

        return sessionDto;
    }

    public PokerSessionDto getActiveSession(Long teamId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

        // Kullanıcının takımda olup olmadığını kontrol et
        if (!teamMemberRepository.existsByTeamAndUser(team, user)) {
            throw new RuntimeException("Bu takımın poker oturumlarını görme yetkiniz yok");
        }

        Optional<PokerSession> activeSession = pokerSessionRepository.findActiveSessionByTeam(team);
        if (activeSession.isEmpty()) {
            return null;
        }

        return convertToDto(activeSession.get(), user);
    }

    public List<PokerSessionDto> getTeamSessions(Long teamId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

        // Kullanıcının takımda olup olmadığını kontrol et
        if (!teamMemberRepository.existsByTeamAndUser(team, user)) {
            throw new RuntimeException("Bu takımın poker oturumlarını görme yetkiniz yok");
        }

        List<PokerSession> sessions = pokerSessionRepository.findByTeamOrderByCreatedAtDesc(team);
        return sessions.stream()
                .map(session -> convertToDto(session, user))
                .collect(Collectors.toList());
    }

    private PokerSessionDto convertToDto(PokerSession session, User currentUser) {
        PokerSessionDto dto = new PokerSessionDto();
        dto.setId(session.getId());
        dto.setTeamId(session.getTeam().getId());
        dto.setTeamName(session.getTeam().getName());
        dto.setStoryTitle(session.getStoryTitle());
        dto.setStoryDescription(session.getStoryDescription());
        dto.setStatus(session.getStatus());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setCompletedAt(session.getCompletedAt());
        dto.setFinalEstimate(session.getFinalEstimate());

        // Created by user info
        UserDto createdByDto = new UserDto();
        createdByDto.setId(String.valueOf(session.getCreatedBy().getId()));
        createdByDto.setFirstName(session.getCreatedBy().getFirstName());
        createdByDto.setLastName(session.getCreatedBy().getLastName());
        createdByDto.setEmail(session.getCreatedBy().getEmail());
        dto.setCreatedBy(createdByDto);

        // Votes
        List<PokerVote> votes = pokerVoteRepository.findByPokerSessionOrderByVotedAtAsc(session);
        dto.setVotes(votes.stream()
                .map(this::convertVoteToDto)
                .collect(Collectors.toList()));
        dto.setTotalVotes(votes.size());

        // Check if current user has voted
        dto.setHasUserVoted(votes.stream()
                .anyMatch(vote -> vote.getUser().getId().equals(currentUser.getId())));

        return dto;
    }

    private PokerVoteDto convertVoteToDto(PokerVote vote) {
        PokerVoteDto dto = new PokerVoteDto();
        dto.setId(vote.getId());
        dto.setSessionId(vote.getPokerSession().getId());
        dto.setVoteValue(vote.getVoteValue());
        dto.setVotedAt(vote.getVotedAt());
        dto.setRevealed(vote.isRevealed());

        UserDto userDto = new UserDto();
        userDto.setId(String.valueOf(vote.getUser().getId()));
        userDto.setFirstName(vote.getUser().getFirstName());
        userDto.setLastName(vote.getUser().getLastName());
        userDto.setEmail(vote.getUser().getEmail());
        dto.setUser(userDto);

        return dto;
    }
}
