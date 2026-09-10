package com.streamforge.serviceimpl;

import com.streamforge.dto.request.EvaluationRequest;
import com.streamforge.dto.response.EvaluationResponse;
import com.streamforge.entity.Evaluation;
import com.streamforge.entity.Show;
import com.streamforge.entity.User;
import com.streamforge.enums.EvaluationDecision;
import com.streamforge.enums.NotificationType;
import com.streamforge.enums.ShowStatus;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.EvaluationMapper;
import com.streamforge.repository.EvaluationRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.EvaluationService;
import com.streamforge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationServiceImpl
        implements EvaluationService {

    private final EvaluationRepository evaluationRepository;

    private final ShowRepository showRepository;

    private final UserRepository userRepository;

    private final EvaluationMapper evaluationMapper;

    private final NotificationService notificationService;


    // =========================================================
    // CREATE EVALUATION
    // =========================================================

    @Override
    public EvaluationResponse createEvaluation(
            EvaluationRequest request
    ) {

        /*
         * -----------------------------------------------------
         * FIND SHOW
         * -----------------------------------------------------
         */

        Show show =
                showRepository
                        .findById(
                                request.getShowId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Show not found with id: "
                                                + request.getShowId()
                                )
                        );


        /*
         * -----------------------------------------------------
         * FIND EVALUATOR
         * -----------------------------------------------------
         */

        User evaluator =
                userRepository
                        .findById(
                                request.getEvaluatorId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Evaluator not found with id: "
                                                + request.getEvaluatorId()
                                )
                        );


        /*
         * -----------------------------------------------------
         * CREATE EVALUATION
         * -----------------------------------------------------
         */

        Evaluation evaluation =
                Evaluation.builder()
                        .show(show)
                        .evaluator(evaluator)
                        .originalityScore(
                                request.getOriginalityScore()
                        )
                        .creativityScore(
                                request.getCreativityScore()
                        )
                        .marketPotentialScore(
                                request.getMarketPotentialScore()
                        )
                        .feasibilityScore(
                                request.getFeasibilityScore()
                        )
                        .overallScore(
                                request.getOverallScore()
                        )
                        .decision(
                                request.getDecision()
                        )
                        .remarks(
                                cleanNullable(
                                        request.getRemarks()
                                )
                        )
                        .build();


        /*
         * -----------------------------------------------------
         * SAVE EVALUATION
         * -----------------------------------------------------
         */

        Evaluation savedEvaluation =
                evaluationRepository.save(
                        evaluation
                );


        /*
         * -----------------------------------------------------
         * UPDATE SHOW WORKFLOW STATUS
         * -----------------------------------------------------
         *
         * APPROVED
         *     -> APPROVED
         *
         * REJECTED
         *     -> REJECTED
         *
         * REVISION_REQUIRED
         *     -> UNDER_REVIEW
         */

        updateShowStatus(
                show,
                request.getDecision()
        );


        /*
         * -----------------------------------------------------
         * SAVE SHOW STATUS
         * -----------------------------------------------------
         */

        showRepository.save(
                show
        );


        /*
         * -----------------------------------------------------
         * NOTIFY SHOW CREATOR
         * -----------------------------------------------------
         */

        notifyCreator(
                show,
                request.getDecision(),
                false
        );


        return evaluationMapper.toResponse(
                savedEvaluation
        );
    }


    // =========================================================
    // GET SHOW EVALUATIONS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationResponse> getShowEvaluations(
            Long showId
    ) {

        /*
         * Verify show exists.
         */

        if (
                !showRepository.existsById(
                        showId
                )
        ) {

            throw new ResourceNotFoundException(
                    "Show not found with id: "
                            + showId
            );
        }


        return evaluationRepository
                .findByShowShowId(
                        showId
                )
                .stream()
                .map(
                        evaluationMapper::toResponse
                )
                .toList();
    }


    // =========================================================
    // GET EVALUATION BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluationById(
            Long evaluationId
    ) {

        return evaluationRepository
                .findById(
                        evaluationId
                )
                .map(
                        evaluationMapper::toResponse
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Evaluation not found with id: "
                                        + evaluationId
                        )
                );
    }


    // =========================================================
    // UPDATE EVALUATION
    // =========================================================

    @Override
    public EvaluationResponse updateEvaluation(
            Long evaluationId,
            EvaluationRequest request
    ) {

        /*
         * -----------------------------------------------------
         * FIND EXISTING EVALUATION
         * -----------------------------------------------------
         */

        Evaluation evaluation =
                evaluationRepository
                        .findById(
                                evaluationId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Evaluation not found with id: "
                                                + evaluationId
                                )
                        );


        /*
         * -----------------------------------------------------
         * SHOW
         * -----------------------------------------------------
         *
         * We intentionally keep the existing evaluation's
         * show relationship.
         *
         * This prevents the client from moving an evaluation
         * to another show during an update.
         */

        Show show =
                evaluation.getShow();


        if (
                show == null
        ) {

            throw new ResourceNotFoundException(
                    "Show associated with evaluation was not found."
            );
        }


        /*
         * -----------------------------------------------------
         * UPDATE SCORES
         * -----------------------------------------------------
         */

        evaluation.setOriginalityScore(
                request.getOriginalityScore()
        );

        evaluation.setCreativityScore(
                request.getCreativityScore()
        );

        evaluation.setMarketPotentialScore(
                request.getMarketPotentialScore()
        );

        evaluation.setFeasibilityScore(
                request.getFeasibilityScore()
        );

        evaluation.setOverallScore(
                request.getOverallScore()
        );


        /*
         * -----------------------------------------------------
         * UPDATE DECISION
         * -----------------------------------------------------
         */

        evaluation.setDecision(
                request.getDecision()
        );


        /*
         * -----------------------------------------------------
         * UPDATE REMARKS
         * -----------------------------------------------------
         */

        evaluation.setRemarks(
                cleanNullable(
                        request.getRemarks()
                )
        );


        /*
         * -----------------------------------------------------
         * SAVE EVALUATION
         * -----------------------------------------------------
         */

        Evaluation savedEvaluation =
                evaluationRepository.save(
                        evaluation
                );


        /*
         * -----------------------------------------------------
         * UPDATE SHOW STATUS
         * -----------------------------------------------------
         */

        updateShowStatus(
                show,
                request.getDecision()
        );


        showRepository.save(
                show
        );


        /*
         * -----------------------------------------------------
         * NOTIFY CREATOR
         * -----------------------------------------------------
         */

        notifyCreator(
                show,
                request.getDecision(),
                true
        );


        return evaluationMapper.toResponse(
                savedEvaluation
        );
    }


    // =========================================================
    // DELETE EVALUATION
    // =========================================================

    @Override
    public void deleteEvaluation(
            Long evaluationId
    ) {

        Evaluation evaluation =
                evaluationRepository
                        .findById(
                                evaluationId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Evaluation not found with id: "
                                                + evaluationId
                        )
                );

        evaluationRepository.delete(
                evaluation
        );
    }


    // =========================================================
    // UPDATE SHOW STATUS
    // =========================================================

    private void updateShowStatus(
            Show show,
            EvaluationDecision decision
    ) {

        if (
                decision == null
        ) {

            return;
        }


        switch (
                decision
        ) {

            case APPROVED:

                show.setStatus(
                        ShowStatus.APPROVED
                );

                break;


            case REJECTED:

                show.setStatus(
                        ShowStatus.REJECTED
                );

                break;


            case REVISION_REQUIRED:

                show.setStatus(
                        ShowStatus.UNDER_REVIEW
                );

                break;


            default:

                /*
                 * Defensive fallback.
                 *
                 * If another enum value is introduced later,
                 * don't unexpectedly change the show status.
                 */

                break;
        }
    }


    // =========================================================
    // CREATOR NOTIFICATION
    // =========================================================

    private void notifyCreator(
            Show show,
            EvaluationDecision decision,
            boolean updated
    ) {

        /*
         * -----------------------------------------------------
         * Validate creator
         * -----------------------------------------------------
         */

        if (
                show.getCreator() == null ||
                show.getCreator().getUserId() == null
        ) {

            return;
        }


        /*
         * -----------------------------------------------------
         * Decision text
         * -----------------------------------------------------
         */

        String decisionText =
                formatDecision(
                        decision
                );


        /*
         * -----------------------------------------------------
         * Notification type
         * -----------------------------------------------------
         */

        NotificationType notificationType =
                getNotificationType(
                        decision
                );


        /*
         * -----------------------------------------------------
         * Title
         * -----------------------------------------------------
         */

        String title;

        if (updated) {

            title =
                    "Evaluation Updated";

        } else {

            title =
                    "Evaluation Completed";
        }


        /*
         * -----------------------------------------------------
         * Message
         * -----------------------------------------------------
         */

        String message =
                "Your show '" +
                        show.getTitle() +
                        "' has been evaluated. " +
                        "Decision: " +
                        decisionText +
                        ".";


        /*
         * Add workflow-specific information.
         */

        if (
                decision ==
                        EvaluationDecision.APPROVED
        ) {

            message +=
                    " Your show has been approved and can proceed to production.";

        } else if (
                decision ==
                        EvaluationDecision.REJECTED
        ) {

            message +=
                    " Your show has been rejected.";

        } else if (
                decision ==
                        EvaluationDecision.REVISION_REQUIRED
        ) {

            message +=
                    " Please review the evaluator remarks and make the required changes.";
        }


        /*
         * -----------------------------------------------------
         * CREATE NOTIFICATION
         * -----------------------------------------------------
         */

        notificationService.createNotification(
                show.getCreator().getUserId(),
                title,
                message,
                notificationType
        );
    }


    // =========================================================
    // NOTIFICATION TYPE
    // =========================================================

    private NotificationType getNotificationType(
            EvaluationDecision decision
    ) {

        if (
                decision ==
                        EvaluationDecision.APPROVED
        ) {

            return NotificationType.SUCCESS;
        }


        if (
                decision ==
                        EvaluationDecision.REJECTED
        ) {

            return NotificationType.ERROR;
        }


        if (
                decision ==
                        EvaluationDecision.REVISION_REQUIRED
        ) {

            return NotificationType.WARNING;
        }


        return NotificationType.INFO;
    }


    // =========================================================
    // DECISION TEXT
    // =========================================================

    private String formatDecision(
            EvaluationDecision decision
    ) {

        if (
                decision == null
        ) {

            return "Not decided";
        }


        switch (
                decision
        ) {

            case APPROVED:

                return "Approved";


            case REJECTED:

                return "Rejected";


            case REVISION_REQUIRED:

                return "Revision required";


            default:

                return decision.name();
        }
    }


    // =========================================================
    // CLEAN NULLABLE STRING
    // =========================================================

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