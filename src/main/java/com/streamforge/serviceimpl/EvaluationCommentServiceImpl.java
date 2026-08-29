package com.streamforge.serviceimpl;

import com.streamforge.dto.response.EvaluationCommentResponse;
import com.streamforge.entity.Evaluation;
import com.streamforge.entity.EvaluationComment;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.EvaluationCommentMapper;
import com.streamforge.repository.EvaluationCommentRepository;
import com.streamforge.repository.EvaluationRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.EvaluationCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationCommentServiceImpl
        implements EvaluationCommentService {

    private final EvaluationCommentRepository commentRepository;

    private final EvaluationRepository evaluationRepository;

    private final UserRepository userRepository;

    private final EvaluationCommentMapper commentMapper;


    // =========================================================
    // ADD COMMENT
    // =========================================================

    @Override
    public EvaluationCommentResponse addComment(
            Long evaluationId,
            Long userId,
            String comment
    ) {

        Evaluation evaluation =
                evaluationRepository.findById(
                        evaluationId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Evaluation not found with id: "
                                        + evaluationId
                        )
                );


        User user =
                userRepository.findById(
                        userId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );


        EvaluationComment evaluationComment =
                EvaluationComment.builder()
                        .evaluation(evaluation)
                        .user(user)
                        .comment(comment.trim())
                        .build();


        EvaluationComment saved =
                commentRepository.save(
                        evaluationComment
                );


        return commentMapper.toResponse(
                saved
        );
    }


    // =========================================================
    // GET COMMENTS
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationCommentResponse>
    getCommentsByEvaluation(
            Long evaluationId
    ) {

        if (!evaluationRepository.existsById(
                evaluationId
        )) {

            throw new ResourceNotFoundException(
                    "Evaluation not found with id: "
                            + evaluationId
            );
        }


        return commentRepository
                .findByEvaluationEvaluationId(
                        evaluationId
                )
                .stream()
                .map(commentMapper::toResponse)
                .toList();
    }


    // =========================================================
    // DELETE COMMENT
    // =========================================================

    @Override
    public void deleteComment(
            Long commentId
    ) {

        EvaluationComment comment =
                commentRepository.findById(
                        commentId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Evaluation comment not found with id: "
                                        + commentId
                        )
                );


        commentRepository.delete(
                comment
        );
    }
}