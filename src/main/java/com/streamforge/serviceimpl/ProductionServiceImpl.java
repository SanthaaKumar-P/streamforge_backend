package com.streamforge.serviceimpl;

import com.streamforge.dto.request.ProductionRequest;
import com.streamforge.dto.response.ProductionResponse;
import com.streamforge.entity.Production;
import com.streamforge.entity.Show;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ProductionMapper;
import com.streamforge.repository.ProductionRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.ProductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionServiceImpl implements ProductionService {

    private final ProductionRepository productionRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final ProductionMapper productionMapper;

    @Override
    public ProductionResponse createProduction(
            ProductionRequest request
    ) {

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: "
                                        + request.getShowId()
                        )
                );

        User producer = userRepository.findById(request.getProducerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Producer not found with id: "
                                        + request.getProducerId()
                        )
                );

        Production production = Production.builder()
                .show(show)
                .producer(producer)
                .productionStatus(request.getProductionStatus())
                .allocatedBudget(request.getAllocatedBudget())
                .actualBudget(request.getActualBudget())
                .startDate(request.getStartDate())
                .expectedEndDate(request.getExpectedEndDate())
                .completionDate(request.getCompletionDate())
                .notes(request.getNotes())
                .build();

        return productionMapper.toResponse(
                productionRepository.save(production)
        );
    }

    @Override
    public ProductionResponse getProductionById(
            Long productionId
    ) {

        return productionRepository.findById(productionId)
                .map(productionMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Production not found with id: "
                                        + productionId
                        )
                );
    }

    @Override
    public List<ProductionResponse> getProductionsByShow(
            Long showId
    ) {

        if (!showRepository.existsById(showId)) {
            throw new ResourceNotFoundException(
                    "Show not found with id: " + showId
            );
        }

        return productionRepository.findByShowShowId(showId)
                .stream()
                .map(productionMapper::toResponse)
                .toList();
    }

    @Override
    public ProductionResponse updateProduction(
            Long productionId,
            ProductionRequest request
    ) {

        Production production =
                productionRepository.findById(productionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Production not found with id: "
                                                + productionId
                                )
                        );

        production.setProductionStatus(
                request.getProductionStatus()
        );

        production.setAllocatedBudget(
                request.getAllocatedBudget()
        );

        production.setActualBudget(
                request.getActualBudget()
        );

        production.setNotes(
                request.getNotes()
        );

        return productionMapper.toResponse(
                productionRepository.save(production)
        );
    }

    @Override
    public void deleteProduction(Long productionId) {

        Production production =
                productionRepository.findById(productionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Production not found with id: "
                                                + productionId
                                )
                        );

        productionRepository.delete(production);
    }
}