package com.streamforge.controller;

import com.streamforge.dto.request.ProductionRequest;
import com.streamforge.dto.response.ProductionResponse;
import com.streamforge.service.ProductionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/productions")
@RequiredArgsConstructor
public class ProductionController {


    private final ProductionService productionService;


    /* =====================================================
       CREATE PRODUCTION
    ===================================================== */

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PRODUCER')")
    public ResponseEntity<ProductionResponse> createProduction(
            @Valid
            @RequestBody
            ProductionRequest request
    ) {

        return ResponseEntity.ok(
                productionService.createProduction(
                        request
                )
        );
    }


    /* =====================================================
       GET ALL PRODUCTIONS
    ===================================================== */

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductionResponse>>
    getAllProductions() {

        return ResponseEntity.ok(
                productionService.getAllProductions()
        );
    }


    /* =====================================================
       GET PRODUCTION BY ID
    ===================================================== */

    @GetMapping("/{productionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductionResponse>
    getProductionById(
            @PathVariable Long productionId
    ) {

        return ResponseEntity.ok(
                productionService.getProductionById(
                        productionId
                )
        );
    }


    /* =====================================================
       GET PRODUCTIONS BY SHOW
    ===================================================== */

    @GetMapping("/show/{showId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductionResponse>>
    getProductionsByShow(
            @PathVariable Long showId
    ) {

        return ResponseEntity.ok(
                productionService.getProductionsByShow(
                        showId
                )
        );
    }


    /* =====================================================
       UPDATE
    ===================================================== */

    @PutMapping("/{productionId}")
    @PreAuthorize("hasAnyRole('ADMIN','PRODUCER')")
    public ResponseEntity<ProductionResponse>
    updateProduction(
            @PathVariable Long productionId,

            @Valid
            @RequestBody
            ProductionRequest request
    ) {

        return ResponseEntity.ok(
                productionService.updateProduction(
                        productionId,
                        request
                )
        );
    }


    /* =====================================================
       DELETE
    ===================================================== */

    @DeleteMapping("/{productionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String>
    deleteProduction(
            @PathVariable Long productionId
    ) {

        productionService.deleteProduction(
                productionId
        );

        return ResponseEntity.ok(
                "Production deleted successfully"
        );
    }
}