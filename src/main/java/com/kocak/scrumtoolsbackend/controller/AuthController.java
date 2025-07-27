package com.kocak.scrumtoolsbackend.controller;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.entity.User;
import com.kocak.scrumtoolsbackend.service.AuthService;
import com.kocak.scrumtoolsbackend.service.UserPreferenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse<AuthResponse> response = authService.login(loginRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        ApiResponse<AuthResponse> response = authService.signup(signupRequest);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        ApiResponse<String> response = authService.logout();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        ApiResponse<UserDto> response = authService.getUserProfile(email);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ApiResponse<Map<String, String>> response = userPreferenceService.getPreferences(user.getId());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/preferences/{key}")
    public ResponseEntity<ApiResponse<String>> getPreference(@PathVariable String key) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ApiResponse<String> response = userPreferenceService.getPreference(user.getId(), key);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/preferences/{key}")
    public ResponseEntity<ApiResponse<String>> setPreference(
            @PathVariable String key,
            @RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        String value = request.get("value");
        ApiResponse<String> response = userPreferenceService.setPreference(user.getId(), key, value);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<String>> setPreferences(@RequestBody Map<String, String> preferences) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ApiResponse<String> response = userPreferenceService.setPreferences(user.getId(), preferences);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/preferences/{key}")
    public ResponseEntity<ApiResponse<String>> deletePreference(@PathVariable String key) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ApiResponse<String> response = userPreferenceService.deletePreference(user.getId(), key);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/preferences")
    public ResponseEntity<ApiResponse<String>> resetPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ApiResponse<String> response = userPreferenceService.resetPreferences(user.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/preferences/defaults")
    public ResponseEntity<ApiResponse<String>> setDefaultPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        ApiResponse<String> response = userPreferenceService.setDefaultPreferences(user.getId());

        return ResponseEntity.ok(response);
    }
}
