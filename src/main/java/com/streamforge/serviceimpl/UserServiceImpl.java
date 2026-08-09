package com.streamforge.serviceimpl;

import com.streamforge.dto.request.UserRequest;
import com.streamforge.dto.response.UserResponse;
import com.streamforge.entity.Role;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.UserMapper;
import com.streamforge.repository.RoleRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserResponse createUser(UserRequest request) {

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found")
                );

        if (request.getPassword() == null ||
                request.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Password is required"
            );
        }

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

        return userMapper.toResponse(
                userRepository.save(user)
        );
    }


    @Override
    public UserResponse getUserById(Long userId) {

        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }


    @Override
    public UserResponse getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }


    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }


    @Override
    public UserResponse updateUser(
            Long userId,
            UserRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmployeeCode(request.getEmployeeCode());
        user.setBio(request.getBio());

        // Update password only when provided
        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        // Update role only when provided
        if (request.getRoleId() != null) {

            Role role = roleRepository.findById(
                    request.getRoleId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Role not found"
                    )
            );

            user.setRole(role);
        }

        return userMapper.toResponse(
                userRepository.save(user)
        );
    }


    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        userRepository.delete(user);
    }
}