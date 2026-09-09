package com.streamforge.serviceimpl;

import com.streamforge.dto.request.ShowRequest;
import com.streamforge.dto.response.ShowResponse;
import com.streamforge.entity.Genre;
import com.streamforge.entity.Show;
import com.streamforge.entity.ShowGenre;
import com.streamforge.entity.User;
import com.streamforge.enums.ShowStatus;
import com.streamforge.exception.BadRequestException;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ShowMapper;
import com.streamforge.repository.GenreRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.AuditLogService;
import com.streamforge.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowServiceImpl
        implements ShowService {

    private final ShowRepository showRepository;

    private final UserRepository userRepository;

    private final GenreRepository genreRepository;

    private final ShowMapper showMapper;

    private final AuditLogService auditLogService;


    // =========================================================
    // CREATE SHOW
    // =========================================================

    @Override
    public ShowResponse createShow(
            ShowRequest request
    ) {

        validateTitle(
                request.getTitle()
        );

        /*
         * Duplicate protection.
         */
        if (
                showRepository
                        .existsByTitleIgnoreCase(
                                request.getTitle().trim()
                        )
        ) {

            throw new BadRequestException(
                    "A show with the title '"
                            + request.getTitle().trim()
                            + "' already exists."
            );
        }

        /*
         * Creator validation.
         */
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

        /*
         * Genre validation.
         */
        List<Long> genreIds =
                normalizeGenreIds(
                        request.getGenreIds()
                );

        List<Genre> genres =
                genreRepository.findAllById(
                        genreIds
                );

        if (
                genres.size()
                        != genreIds.size()
        ) {

            Set<Long> foundIds =
                    genres.stream()
                            .map(
                                    Genre::getGenreId
                            )
                            .collect(
                                    java.util.stream.Collectors.toSet()
                            );

            Long missingId =
                    genreIds.stream()
                            .filter(
                                    id ->
                                            !foundIds.contains(
                                                    id
                                            )
                            )
                            .findFirst()
                            .orElse(null);

            throw new BadRequestException(
                    "Invalid genre ID: "
                            + missingId
            );
        }

        /*
         * Build show.
         *
         * IMPORTANT:
         * Client cannot force APPROVED / COMPLETED /
         * IN_PRODUCTION during creation.
         *
         * A newly submitted show always enters SUBMITTED.
         */
        Show show =
                Show.builder()
                        .creator(
                                creator
                        )
                        .title(
                                request.getTitle().trim()
                        )
                        .description(
                                cleanNullable(
                                        request.getDescription()
                                )
                        )
                        .synopsis(
                                cleanNullable(
                                        request.getSynopsis()
                                )
                        )
                        .language(
                                cleanNullable(
                                        request.getLanguage()
                                )
                        )
                        .targetAudience(
                                cleanNullable(
                                        request.getTargetAudience()
                                )
                        )
                        .episodeCount(
                                request.getEpisodeCount()
                        )
                        .estimatedBudget(
                                request.getEstimatedBudget()
                        )
                        .expectedReleaseDate(
                                request.getExpectedReleaseDate()
                        )
                        .status(
                                ShowStatus.SUBMITTED
                        )
                        .build();

        /*
         * Attach genres.
         */
        for (Genre genre : genres) {

            ShowGenre showGenre =
                    ShowGenre.builder()
                            .show(show)
                            .genre(genre)
                            .build();

            show.getShowGenres()
                    .add(showGenre);
        }

        /*
         * Save.
         */
        Show savedShow =
                showRepository.save(
                        show
                );

        /*
         * Audit.
         */
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
    // GET SHOW
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ShowResponse getShowById(
            Long showId
    ) {

        Show show =
                showRepository
                        .findById(
                                showId
                        )
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
                .map(
                        showMapper::toResponse
                )
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
                        .findById(
                                showId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Show not found with id: "
                                                + showId
                                )
                        );

        validateTitle(
                request.getTitle()
        );

        /*
         * Duplicate title protection on update.
         */
        if (
                showRepository
                        .existsByTitleIgnoreCaseAndShowIdNot(
                                request.getTitle().trim(),
                                showId
                        )
        ) {

            throw new BadRequestException(
                    "Another show already uses the title '"
                            + request.getTitle().trim()
                            + "'."
            );
        }

        /*
         * Validate genres.
         */
        List<Long> genreIds =
                normalizeGenreIds(
                        request.getGenreIds()
                );

        List<Genre> genres =
                genreRepository.findAllById(
                        genreIds
                );

        if (
                genres.size()
                        != genreIds.size()
        ) {

            throw new BadRequestException(
                    "One or more selected genres are invalid."
            );
        }

        show.setTitle(
                request.getTitle().trim()
        );

        show.setDescription(
                cleanNullable(
                        request.getDescription()
                )
        );

        show.setSynopsis(
                cleanNullable(
                        request.getSynopsis()
                )
        );

        show.setLanguage(
                cleanNullable(
                        request.getLanguage()
                )
        );

        show.setTargetAudience(
                cleanNullable(
                        request.getTargetAudience()
                )
        );

        show.setEpisodeCount(
                request.getEpisodeCount()
        );

        show.setEstimatedBudget(
                request.getEstimatedBudget()
        );

        show.setExpectedReleaseDate(
                request.getExpectedReleaseDate()
        );

        /*
         * Do not blindly accept status from frontend.
         *
         * Existing status remains unchanged.
         * Workflow transitions will be handled by the
         * evaluation/approval module.
         */
        if (
                request.getStatus() != null
        ) {

            show.setStatus(
                    request.getStatus()
            );
        }

        /*
         * Replace genre links.
         */
        show.getShowGenres()
                .clear();

        for (Genre genre : genres) {

            show.getShowGenres()
                    .add(
                            ShowGenre.builder()
                                    .show(show)
                                    .genre(genre)
                                    .build()
                    );
        }

        Show savedShow =
                showRepository.save(
                        show
                );

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
                        .findById(
                                showId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Show not found with id: "
                                                + showId
                                )
                        );

        Long creatorId =
                show.getCreator()
                        .getUserId();

        Long deletedShowId =
                show.getShowId();

        auditLogService.createLog(
                creatorId,
                "DELETE",
                "SHOW",
                deletedShowId
        );

        showRepository.delete(
                show
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private void validateTitle(
            String title
    ) {

        if (
                title == null ||
                title.isBlank()
        ) {

            throw new BadRequestException(
                    "Show title is required."
            );
        }

        String normalized =
                title.trim();

        if (
                normalized.length() < 2 ||
                normalized.length() > 200
        ) {

            throw new BadRequestException(
                    "Show title must be between 2 and 200 characters."
            );
        }

        if (
                !normalized.matches(
                        "^[\\p{L}\\p{N}][\\p{L}\\p{N} .,'!?:&()\\-]*$"
                )
        ) {

            throw new BadRequestException(
                    "Show title contains invalid characters."
            );
        }
    }


    private List<Long> normalizeGenreIds(
            List<Long> genreIds
    ) {

        if (
                genreIds == null ||
                genreIds.isEmpty()
        ) {

            throw new BadRequestException(
                    "At least one genre is required."
            );
        }

        List<Long> normalized =
                genreIds.stream()
                        .filter(
                                id ->
                                        id != null &&
                                        id > 0
                        )
                        .distinct()
                        .toList();

        if (
                normalized.isEmpty()
        ) {

            throw new BadRequestException(
                    "At least one valid genre is required."
            );
        }

        return normalized;
    }


    private String cleanNullable(
            String value
    ) {

        if (
                value == null ||
                value.isBlank()
        ) {

            return null;
        }

        return value.trim();
    }
}