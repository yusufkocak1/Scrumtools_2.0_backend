package com.kocak.scrumtoolsbackend.controller;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.entity.User;
import com.kocak.scrumtoolsbackend.service.RetrospectiveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/retrospective")
@CrossOrigin(origins = "*")
public class RetrospectiveController {

    @Autowired
    private RetrospectiveService retrospectiveService;

    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<RetrospectiveSessionDto>> createRetrospectiveSession(
            @Valid @RequestBody CreateRetrospectiveSessionRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            RetrospectiveSessionDto sessionDto = retrospectiveService.createRetrospectiveSession(request, currentUser);

            ApiResponse<RetrospectiveSessionDto> response = ApiResponse.success(sessionDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            ApiResponse<RetrospectiveSessionDto> response = ApiResponse.error(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sessions/{id}/items")
    public ResponseEntity<ApiResponse<RetrospectiveItemDto>> addRetrospectiveItem(
            @PathVariable Long id,
            @Valid @RequestBody AddRetrospectiveItemRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            RetrospectiveItemDto itemDto = retrospectiveService.addRetrospectiveItem(id, request, currentUser);

            ApiResponse<RetrospectiveItemDto> response = ApiResponse.success(itemDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            ApiResponse<RetrospectiveItemDto> response = ApiResponse.error(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/items/{id}/vote")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> voteRetrospectiveItem(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            Integer votes = retrospectiveService.voteRetrospectiveItem(id, currentUser);

            Map<String, Integer> data = Map.of("votes", votes);
            ApiResponse<Map<String, Integer>> response = ApiResponse.success(data);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            ApiResponse<Map<String, Integer>> response = ApiResponse.error(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sessions/{id}/actions")
    public ResponseEntity<ApiResponse<ActionItemDto>> createActionItem(
            @PathVariable Long id,
            @Valid @RequestBody CreateActionItemRequest request) {
        try {
            ActionItemDto actionItemDto = retrospectiveService.createActionItem(id, request);

            ApiResponse<ActionItemDto> response = ApiResponse.success(actionItemDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            ApiResponse<ActionItemDto> response = ApiResponse.error(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/sessions/{id}/results")
    public ResponseEntity<ApiResponse<RetrospectiveResultsDto>> getRetrospectiveResults(@PathVariable Long id) {
        try {
            RetrospectiveResultsDto resultsDto = retrospectiveService.getRetrospectiveResults(id);

            ApiResponse<RetrospectiveResultsDto> response = ApiResponse.success(resultsDto);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            ApiResponse<RetrospectiveResultsDto> response = ApiResponse.error(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
