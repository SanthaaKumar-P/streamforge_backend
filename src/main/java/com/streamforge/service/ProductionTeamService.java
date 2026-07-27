package com.streamforge.service;

import com.streamforge.dto.response.ProductionTeamResponse;

import java.util.List;

public interface ProductionTeamService {

    ProductionTeamResponse assignMember(
            Long productionId,
            Long userId,
            String role
    );

    List<ProductionTeamResponse> getTeamMembers(Long productionId);

    void removeMember(Long teamId);

}