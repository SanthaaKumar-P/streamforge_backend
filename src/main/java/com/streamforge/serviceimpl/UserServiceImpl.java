package com.streamforge.serviceimpl;

import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.dto.request.UserRequest;
import com.streamforge.dto.response.UserResponse;
import com.streamforge.entity.Role;
import com.streamforge.entity.User;
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
                        new ResourceNotFoundException(
                                "Role not found with id: " + request.getRoleId()
                        )
                );

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode("defaultPassword"))
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
                                "User not found with id: " + userId
                        )
                );
    }

    @Override
    public UserResponse getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
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
                                "User not found with id: " + userId
                        )
                );

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setBio(request.getBio());

        return userMapper.toResponse(
                userRepository.save(user)
        );
    }

    @Override
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        userRepository.delete(user);
    }
}