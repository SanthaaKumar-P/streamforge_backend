package com.streamforge.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long userId;

    private String fullName;

    private String username;

    private String email;

    private String phone;

    private Boolean isActive;

    private RoleResponse role;

}