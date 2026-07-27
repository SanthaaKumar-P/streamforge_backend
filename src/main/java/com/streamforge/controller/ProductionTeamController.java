package com.streamforge.controller;

import com.streamforge.dto.response.ProductionTeamResponse;
import com.streamforge.service.ProductionTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production-team")
@RequiredArgsConstructor
public class ProductionTeamController {

    private final ProductionTeamService productionTeamService;

    @PostMapping("/{productionId}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','PRODUCER')")
    public ResponseEntity<ProductionTeamResponse> assignMember(
            @PathVariable Long productionId,
            @PathVariable Long userId,
            @RequestParam String role) {

        return ResponseEntity.ok(
                productionTeamService.assignMember(
                        productionId,
                        userId,
                        role
                )
        );
    }

    @GetMapping("/{productionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductionTeamResponse>> getTeamMembers(
            @PathVariable Long productionId) {

        return ResponseEntity.ok(
                productionTeamService.getTeamMembers(productionId)
        );
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("hasAnyRole('ADMIN','PRODUCER')")
    public ResponseEntity<String> removeMember(
            @PathVariable Long teamId) {

        productionTeamService.removeMember(teamId);

        return ResponseEntity.ok("Production team member removed successfully");
    }

}