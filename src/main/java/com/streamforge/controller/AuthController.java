package com.streamforge.controller;

import com.streamforge.dto.request.auth.LoginRequest;
import com.streamforge.dto.request.auth.RegisterRequest;
import com.streamforge.dto.response.LoginResponse;
import com.streamforge.dto.response.UserResponse;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.UserMapper;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    // =========================================================
    // REGISTER
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @RequestBody RegisterRequest request
    ) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }


    // =========================================================
    // CURRENT LOGGED-IN USER
    // =========================================================

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication
    ) {

        String username =
                authentication.getName();


        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with username: "
                                                + username
                                )
                        );


        return ResponseEntity.ok(
                userMapper.toResponse(user)
        );
    }
}