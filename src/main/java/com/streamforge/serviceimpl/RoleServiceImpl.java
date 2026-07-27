package com.streamforge.serviceimpl;

import com.streamforge.dto.response.RoleResponse;
import com.streamforge.entity.Role;
import com.streamforge.mapper.RoleMapper;
import com.streamforge.repository.RoleRepository;
import com.streamforge.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {


    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;


    @Override
    public RoleResponse getRoleById(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(
                        () -> new RuntimeException("Role not found")
                );

        return roleMapper.toResponse(role);
    }


    @Override
    public List<RoleResponse> getAllRoles() {

        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();

    }

}