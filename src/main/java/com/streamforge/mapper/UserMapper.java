package com.streamforge.mapper;

import com.streamforge.dto.response.UserResponse;
import com.streamforge.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    private final RoleMapper roleMapper;


    public UserMapper(RoleMapper roleMapper){
        this.roleMapper = roleMapper;
    }


    public UserResponse toResponse(User user){

        return UserResponse.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .role(
                    user.getRole()!=null ?
                    roleMapper.toResponse(user.getRole())
                    : null
                )
                .build();
    }

}