package com.streamforge.service;

import com.streamforge.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse getRoleById(Long roleId);

    List<RoleResponse> getAllRoles();

}