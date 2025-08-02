package com.kocak.scrumtoolsbackend.service;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.entity.Team;
import com.kocak.scrumtoolsbackend.entity.TeamMember;
import com.kocak.scrumtoolsbackend.entity.User;
import com.kocak.scrumtoolsbackend.repository.TeamMemberRepository;
import com.kocak.scrumtoolsbackend.repository.TeamRepository;
import com.kocak.scrumtoolsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    public ApiResponse<TeamDto> createTeam(CreateTeamRequest request, String creatorEmail) {
        try {
            // Find creator user
            User creator = userRepository.findByEmail(creatorEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // Create team
            Team team = new Team(request.getName(), request.getDescription(), creator);
            Team savedTeam = teamRepository.save(team);

            // Add creator as ADMIN member
            Set<TeamMember.TeamRole> creatorRoles = new HashSet<>();
            creatorRoles.add(TeamMember.TeamRole.ADMIN);
            TeamMember creatorMember = new TeamMember(savedTeam, creator, creatorRoles);
            creatorMember.approve();
            teamMemberRepository.save(creatorMember);
            savedTeam.addMember(creatorMember);

            // Add members if provided (default MEMBER role)
            if (request.getMembers() != null && !request.getMembers().isEmpty()) {
                for (String memberEmail : request.getMembers()) {
                    Optional<User> memberUser = userRepository.findByEmail(memberEmail);
                    if (memberUser.isPresent() && !memberUser.get().equals(creator)) {
                        // Default constructor zaten MEMBER rolü ekler
                        TeamMember teamMember = new TeamMember(savedTeam, memberUser.get());
                        teamMemberRepository.save(teamMember);
                        savedTeam.addMember(teamMember);
                    }
                }
            }

            TeamDto teamDto = new TeamDto(savedTeam);
            return ApiResponse.success(teamDto);

        } catch (Exception e) {
            return ApiResponse.error("Takım oluşturulurken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<List<TeamDto>> getUserTeams(String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            List<Team> teams = teamRepository.findAllUserTeams(user);
            List<TeamDto> teamDtos = teams.stream().map(TeamDto::new).collect(Collectors.toList());

            return ApiResponse.success(teamDtos);

        } catch (Exception e) {
            return ApiResponse.error("Takımlar alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<TeamDto> getTeamDetails(Long teamId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

            // Check if user has access to this team
            boolean hasAccess = team.getCreatedBy().equals(user) || teamMemberRepository.existsByTeamAndUser(team, user);

            if (!hasAccess) {
                return ApiResponse.error("Bu takıma erişim yetkiniz bulunmamaktadır");
            }

            TeamDto teamDto = new TeamDto(team);
            return ApiResponse.success(teamDto);

        } catch (Exception e) {
            return ApiResponse.error("Takım detayları alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<String> addTeamMember(Long teamId, AddTeamMemberRequest request, String requesterEmail) {
        try {
            User requester = userRepository.findByEmail(requesterEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

            // Check if requester is team creator or admin
            boolean isAuthorized = team.getCreatedBy().equals(requester);
            if (!isAuthorized) {
                Optional<TeamMember> requesterMembership = teamMemberRepository.findByTeamAndUser(team, requester);
                isAuthorized = requesterMembership.isPresent() && requesterMembership.get().isAdmin();
            }

            if (!isAuthorized) {
                return ApiResponse.error("Bu işlem için yetkiniz bulunmamaktadır");
            }

            // Find user to add
            User userToAdd = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Eklenecek kullanıcı bulunamadı"));

            // Check if user is already a member
            if (teamMemberRepository.existsByTeamAndUser(team, userToAdd)) {
                return ApiResponse.error("Kullanıcı zaten takım üyesidir");
            }

            // Add member with specified role (default MEMBER)
            TeamMember.TeamRole role;
            try {
                role = TeamMember.TeamRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                role = TeamMember.TeamRole.OBSERVER;
            }

            // Default constructor ile MEMBER rolü ekler, sonra istenen rolü de ekler
            TeamMember teamMember = new TeamMember(team, userToAdd);
            if (role != TeamMember.TeamRole.OBSERVER) {
                teamMember.addRole(role);
            }
            teamMemberRepository.save(teamMember);

            return ApiResponse.success("Üye başarıyla eklendi");

        } catch (Exception e) {
            return ApiResponse.error("Üye eklenirken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<String> removeTeamMember(Long teamId, Long memberId, String requesterEmail) {
        try {
            User requester = userRepository.findByEmail(requesterEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

            // Check if requester is team creator or admin
            boolean isAuthorized = team.getCreatedBy().equals(requester);
            if (!isAuthorized) {
                Optional<TeamMember> requesterMembership = teamMemberRepository.findByTeamAndUser(team, requester);
                isAuthorized = requesterMembership.isPresent() && requesterMembership.get().isAdmin();
            }

            if (!isAuthorized) {
                return ApiResponse.error("Bu işlem için yetkiniz bulunmamaktadır");
            }

            // Find member to remove
            User userToRemove = userRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Çıkarılacak kullanıcı bulunamadı"));

            // Cannot remove team creator
            if (team.getCreatedBy().equals(userToRemove)) {
                return ApiResponse.error("Takım oluşturucusu takımdan çıkarılamaz");
            }

            // Remove member
            teamMemberRepository.deleteByTeamAndUser(team, userToRemove);

            return ApiResponse.success("Üye başarıyla çıkarıldı");

        } catch (Exception e) {
            return ApiResponse.error("Üye çıkarılırken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<TeamDto> joinTeamByCode(String inviteCode, String userEmail) {
        try {
            // Find user
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // Find team by invite code
            Team team = teamRepository.findByInviteCode(inviteCode).orElse(null);

            if (team == null) {
                return ApiResponse.error("Geçersiz davet kodu");
            }

            // Check if user is already a member
            Optional<TeamMember> existingMembership = teamMemberRepository.findByTeamAndUser(team, user);
            if (existingMembership.isPresent()) {
                if (existingMembership.get().isActive()) {
                    return ApiResponse.error("Bu takımın zaten aktif üyesisiniz");
                } else if (existingMembership.get().isPending()) {
                    return ApiResponse.error("Bu takıma katılma isteğiniz zaten onay bekliyor");
                } else {
                    return ApiResponse.error("Bu takıma katılma isteğiniz reddedilmiş");
                }
            }

            // Add user as PENDING member (default MEMBER role)
            TeamMember teamMember = new TeamMember(team, user);
            teamMemberRepository.save(teamMember);

            // Return team details
            TeamDto teamDto = new TeamDto(team);
            return ApiResponse.success(teamDto);

        } catch (Exception e) {
            return ApiResponse.error("Takıma katılırken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<String> generateNewInviteCode(Long teamId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

            // Check if user is admin of the team
            Optional<TeamMember> membership = teamMemberRepository.findByTeamAndUser(team, user);
            if (membership.isEmpty() || !membership.get().isAdmin()) {
                return ApiResponse.error("Bu işlem için admin yetkisi gereklidir");
            }

            // Generate new invite code
            team.generateInviteCode();
            teamRepository.save(team);

            return ApiResponse.success(team.getInviteCode());

        } catch (Exception e) {
            return ApiResponse.error("Davet kodu oluşturulurken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<List<TeamMemberDto>> getPendingMembers(Long teamId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

            // Check if user is admin of the team
            Optional<TeamMember> membership = teamMemberRepository.findByTeamAndUser(team, user);
            if (membership.isEmpty() || !membership.get().isAdmin()) {
                return ApiResponse.error("Bu işlem için admin yetkisi gereklidir");
            }

            // Get pending members
            List<TeamMember> pendingMembers = teamMemberRepository.findByTeamAndStatus(team, TeamMember.MemberStatus.PENDING);
            List<TeamMemberDto> pendingMemberDtos = pendingMembers.stream().map(TeamMemberDto::new).collect(Collectors.toList());

            return ApiResponse.success(pendingMemberDtos);

        } catch (Exception e) {
            return ApiResponse.error("Bekleyen üyeler alınırken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<String> approveMember(Long teamId, ApproveMemberRequest request, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));


            User member = userRepository.findById(request.getMemberId()).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

            // Check if user is admin of the team
            Optional<TeamMember> adminMembership = teamMemberRepository.findByTeamAndUser(team, user);
            if (adminMembership.isEmpty() || !adminMembership.get().isAdmin()) {
                return ApiResponse.error("Bu işlem için admin yetkisi gereklidir");
            }

            // Find the member to approve
            TeamMember memberToApprove = teamMemberRepository.findByTeamAndUser(team, member).orElseThrow(() -> new RuntimeException("Üye bulunamadı"));

            // Check if member belongs to this team
            if (!memberToApprove.getTeam().getId().equals(teamId)) {
                return ApiResponse.error("Üye bu takıma ait değil");
            }

            // Check if member is pending
            if (!memberToApprove.isPending()) {
                return ApiResponse.error("Bu üye zaten onaylanmış veya reddedilmiş");
            }

            // Approve member
            memberToApprove.approve();

            // Set roles if provided
            if (request.getRole() != null) {
                Set<TeamMember.TeamRole> newRoles = new HashSet<>();
                try {
                    TeamMember.TeamRole role = TeamMember.TeamRole.valueOf(request.getRole().toUpperCase());
                    newRoles.add(role);
                    memberToApprove.setRoles(newRoles);

                } catch (IllegalArgumentException e) {
                    return ApiResponse.error("Geçersiz rol: " + request.getRole());
                }
            }
            teamMemberRepository.save(memberToApprove);
            return ApiResponse.success("Üye başarıyla onaylandı");

        } catch (Exception e) {
            return ApiResponse.error("Üye onaylanırken bir hata oluştu: " + e.getMessage());
        }
    }

    public ApiResponse<String> rejectMember(Long teamId, Long memberId, String userEmail) {
        try {
            User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Takım bulunamadı"));

            User member = userRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // Check if user is admin of the team
            Optional<TeamMember> adminMembership = teamMemberRepository.findByTeamAndUser(team, user);
            if (adminMembership.isEmpty() || !adminMembership.get().isAdmin()) {
                return ApiResponse.error("Bu işlem için admin yetkisi gereklidir");
            }


            // Find the member to reject
            TeamMember memberToReject = teamMemberRepository.findByTeamAndUser(team, member).orElseThrow(() -> new RuntimeException("Üye bulunamadı"));

            // Check if member belongs to this team
            if (!memberToReject.getTeam().getId().equals(teamId)) {
                return ApiResponse.error("Üye bu takıma ait değil");
            }

            // Check if member is pending
            if (!memberToReject.isPending()) {
                return ApiResponse.error("Bu üye zaten onaylanmış veya reddedilmiş");
            }

            // Reject member (or remove completely)
            teamMemberRepository.delete(memberToReject);

            return ApiResponse.success("Üye başarıyla reddedildi");

        } catch (Exception e) {
            return ApiResponse.error("Üye reddedilirken bir hata oluştu: " + e.getMessage());
        }
    }
}
