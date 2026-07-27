
package com.streamforge.mapper;

import com.streamforge.dto.response.ProductionTeamResponse;
import com.streamforge.entity.ProductionTeam;
import org.springframework.stereotype.Component;

@Component
public class ProductionTeamMapper {

    public ProductionTeamResponse toResponse(ProductionTeam team){

        return ProductionTeamResponse.builder()
                .teamId(team.getTeamId())
                .role(team.getRole())
                .productionId(
                        team.getProduction().getProductionId()
                )
                .userId(
                        team.getUser().getUserId()
                )
                .build();

    }

}