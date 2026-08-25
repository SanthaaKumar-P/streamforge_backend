package com.streamforge.mapper;

import com.streamforge.dto.response.UserResponse;
import com.streamforge.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleMapper roleMapper;

    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public UserResponse toResponse(User user) {

        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .employeeCode(user.getEmployeeCode())
                .bio(user.getBio())
                .isActive(user.getIsActive())
                .role(
                        user.getRole() != null
                                ? roleMapper.toResponse(user.getRole())
                                : null
                )
                .build();
    }
}