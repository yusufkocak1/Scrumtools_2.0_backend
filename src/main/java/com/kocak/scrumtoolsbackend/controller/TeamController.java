package com.kocak.scrumtoolsbackend.controller;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeamDto>> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ApiResponse<TeamDto> response = teamService.createTeam(request, userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamSummaryDto>>> getTeams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ApiResponse<List<TeamSummaryDto>> response = teamService.getUserTeams(userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamDto>> getTeamDetails(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ApiResponse<TeamDto> response = teamService.getTeamDetails(id, userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<String>> addTeamMember(
            @PathVariable Long id,
            @Valid @RequestBody AddTeamMemberRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ApiResponse<String> response = teamService.addTeamMember(id, request, userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<ApiResponse<String>> removeTeamMember(
            @PathVariable Long id,
            @PathVariable Long memberId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ApiResponse<String> response = teamService.removeTeamMember(id, memberId, userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<TeamDto>> joinTeamByCode(@Valid @RequestBody JoinTeamRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        ApiResponse<TeamDto> response = teamService.joinTeamByCode(request.getTeamCode(), userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
