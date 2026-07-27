package com.streamforge.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionTeamResponse {

    private Long teamId;

    private String role;

    private Long productionId;

    private Long userId;

}