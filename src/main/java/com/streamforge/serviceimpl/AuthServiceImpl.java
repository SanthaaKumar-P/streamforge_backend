package com.streamforge.serviceimpl;

import com.streamforge.dto.request.auth.LoginRequest;
import com.streamforge.dto.request.auth.RegisterRequest;
import com.streamforge.dto.response.LoginResponse;
import com.streamforge.dto.response.UserResponse;
import com.streamforge.entity.Role;
import com.streamforge.entity.Session;
import com.streamforge.entity.User;
import com.streamforge.exception.BadRequestException;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.UserMapper;
import com.streamforge.repository.RoleRepository;
import com.streamforge.repository.SessionRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.security.JwtService;
import com.streamforge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    public LoginResponse register(
            RegisterRequest request
    ) {

        if (userRepository.existsByUsername(request.getUsername())) {

            throw new BadRequestException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new BadRequestException(
                    "Email already exists"
            );
        }

        // Default role = CREATOR
        Role role = roleRepository.findByRoleName("CREATOR")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Default role CREATOR not found"
                        )
                );

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .phone(request.getPhone())
                .employeeCode(request.getEmployeeCode())
                .bio(request.getBio())
                .role(role)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getUsername()
        );

        Session session = Session.builder()
                .user(savedUser)
                .accessToken(token)
                .loginTime(LocalDateTime.now())
                .expiryTime(
                        LocalDateTime.now().plusDays(1)
                )
                .isActive(true)
                .build();

        sessionRepository.save(session);

        UserResponse response =
                userMapper.toResponse(savedUser);

        return LoginResponse.builder()
                .accessToken(token)
                .user(response)
                .build();
    }

    @Override
    public LoginResponse login(
            LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(
                request.getUsername()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with username: "
                                + request.getUsername()
                )
        );

        String token = jwtService.generateToken(
                user.getUsername()
        );

        Session session = Session.builder()
                .user(user)
                .accessToken(token)
                .loginTime(LocalDateTime.now())
                .expiryTime(
                        LocalDateTime.now().plusDays(1)
                )
                .isActive(true)
                .build();

        sessionRepository.save(session);

        return LoginResponse.builder()
                .accessToken(token)
                .user(
                        userMapper.toResponse(user)
                )
                .build();
    }
}