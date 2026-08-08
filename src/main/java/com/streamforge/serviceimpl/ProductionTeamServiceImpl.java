package com.streamforge.serviceimpl;

import com.streamforge.dto.response.ProductionTeamResponse;
import com.streamforge.entity.Production;
import com.streamforge.entity.ProductionTeam;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ProductionTeamMapper;
import com.streamforge.repository.ProductionRepository;
import com.streamforge.repository.ProductionTeamRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.ProductionTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionTeamServiceImpl implements ProductionTeamService {

    private final ProductionTeamRepository teamRepository;
    private final ProductionRepository productionRepository;
    private final UserRepository userRepository;
    private final ProductionTeamMapper teamMapper;

    @Override
    public ProductionTeamResponse assignMember(
            Long productionId,
            Long userId,
            String role
    ) {

        Production production =
                productionRepository.findById(productionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Production not found with id: "
                                                + productionId
                                )
                        );

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                        );

        ProductionTeam team =
                ProductionTeam.builder()
                        .production(production)
                        .user(user)
                        .role(role)
                        .build();

        return teamMapper.toResponse(
                teamRepository.save(team)
        );
    }

    @Override
    public List<ProductionTeamResponse> getTeamMembers(
            Long productionId
    ) {

        if (!productionRepository.existsById(productionId)) {
            throw new ResourceNotFoundException(
                    "Production not found with id: " + productionId
            );
        }

        return teamRepository
                .findByProductionProductionId(productionId)
                .stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Override
    public void removeMember(Long teamId) {

        ProductionTeam team =
                teamRepository.findById(teamId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Production team member not found with id: "
                                                + teamId
                                )
                        );

        teamRepository.delete(team);
    }
}