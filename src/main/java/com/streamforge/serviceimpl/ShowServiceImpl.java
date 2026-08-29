package com.streamforge.serviceimpl;

import com.streamforge.dto.request.ShowRequest;
import com.streamforge.dto.response.ShowResponse;
import com.streamforge.entity.Show;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ShowMapper;
import com.streamforge.repository.ShowRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.AuditLogService;
import com.streamforge.service.ShowService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;

    private final UserRepository userRepository;

    private final ShowMapper showMapper;

    private final AuditLogService auditLogService;


    // =========================================================
    // CREATE SHOW
    // =========================================================

    @Override
    public ShowResponse createShow(
            ShowRequest request
    ) {

        User creator =
                userRepository
                        .findById(
                                request.getCreatorId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Creator not found with id: "
                                                + request.getCreatorId()
                                )
                        );


        Show show =
                Show.builder()
                        .creator(creator)
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .synopsis(request.getSynopsis())
                        .language(request.getLanguage())
                        .targetAudience(request.getTargetAudience())
                        .estimatedBudget(request.getEstimatedBudget())
                        .expectedReleaseDate(
                                request.getExpectedReleaseDate()
                        )
                        .status(request.getStatus())
                        .build();


        Show savedShow =
                showRepository.save(show);


        // =====================================================
        // CREATE AUDIT LOG
        // =====================================================

        auditLogService.createLog(
                creator.getUserId(),
                "CREATE",
                "SHOW",
                savedShow.getShowId()
        );


        return showMapper.toResponse(
                savedShow
        );
    }


    // =========================================================
    // GET SHOW BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ShowResponse getShowById(
            Long showId
    ) {

        Show show =
                showRepository
                        .findById(showId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Show not found with id: "
                                                + showId
                                )
                        );


        return showMapper.toResponse(
                show
        );
    }


    // =========================================================
    // GET ALL SHOWS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ShowResponse> getAllShows() {

        return showRepository
                .findAll()
                .stream()
                .map(showMapper::toResponse)
                .toList();
    }


    // =========================================================
    // UPDATE SHOW
    // =========================================================

    @Override
    public ShowResponse updateShow(
            Long showId,
            ShowRequest request
    ) {

        Show show =
                showRepository
                        .findById(showId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Show not found with id: "
                                                + showId
                                )
                        );


        show.setTitle(
                request.getTitle()
        );

        show.setDescription(
                request.getDescription()
        );

        show.setSynopsis(
                request.getSynopsis()
        );

        show.setLanguage(
                request.getLanguage()
        );

        show.setTargetAudience(
                request.getTargetAudience()
        );

        show.setEstimatedBudget(
                request.getEstimatedBudget()
        );

        show.setExpectedReleaseDate(
                request.getExpectedReleaseDate()
        );

        show.setStatus(
                request.getStatus()
        );


        Show savedShow =
                showRepository.save(show);


        // =====================================================
        // CREATE AUDIT LOG
        // =====================================================

        auditLogService.createLog(
                show.getCreator().getUserId(),
                "UPDATE",
                "SHOW",
                savedShow.getShowId()
        );


        return showMapper.toResponse(
                savedShow
        );
    }


    // =========================================================
    // DELETE SHOW
    // =========================================================

    @Override
    public void deleteShow(
            Long showId
    ) {

        Show show =
                showRepository
                        .findById(showId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Show not found with id: "
                                                + showId
                                )
                        );


        Long creatorId =
                show.getCreator().getUserId();


        Long deletedShowId =
                show.getShowId();


        // =====================================================
        // CREATE AUDIT LOG BEFORE DELETE
        // =====================================================

        auditLogService.createLog(
                creatorId,
                "DELETE",
                "SHOW",
                deletedShowId
        );


        // =====================================================
        // DELETE SHOW
        // =====================================================

        showRepository.delete(show);
    }
}