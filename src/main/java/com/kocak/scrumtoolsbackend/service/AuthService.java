package com.kocak.scrumtoolsbackend.service;

import com.kocak.scrumtoolsbackend.dto.*;
import com.kocak.scrumtoolsbackend.entity.User;
import com.kocak.scrumtoolsbackend.repository.UserRepository;
import com.kocak.scrumtoolsbackend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public ApiResponse<AuthResponse> login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();

            String token = jwtUtil.generateToken(user);

            UserDto userDto = new UserDto(user);
            AuthResponse authResponse = new AuthResponse(userDto, token);

            return ApiResponse.success(authResponse);

        } catch (AuthenticationException e) {
            return ApiResponse.error("Geçersiz e-posta veya şifre");
        }
    }

    public ApiResponse<AuthResponse> signup(SignupRequest signupRequest) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(signupRequest.getEmail())) {
                return ApiResponse.error("E-posta adresi zaten kullanımda");
            }

            // Create new user
            User user = new User();
            user.setFirstName(signupRequest.getFirstName());
            user.setLastName(signupRequest.getLastName());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

            User savedUser = userRepository.save(user);

            // Generate token
            String token = jwtUtil.generateToken(savedUser);

            UserDto userDto = new UserDto(savedUser);
            AuthResponse authResponse = new AuthResponse(userDto, token);

            return ApiResponse.success(authResponse);

        } catch (Exception e) {
            return ApiResponse.error("Kayıt işlemi sırasında bir hata oluştu");
        }
    }

    public ApiResponse<String> logout() {
        // For JWT, logout is typically handled on the client side
        // by removing the token from storage
        return ApiResponse.success("Başarıyla çıkış yapıldı");
    }

    public ApiResponse<UserDto> getUserProfile(String email) {
        try {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            UserDto userDto = new UserDto(user);
            return ApiResponse.success(userDto);

        } catch (Exception e) {
            return ApiResponse.error("Kullanıcı profili alınamadı");
        }
    }
}
