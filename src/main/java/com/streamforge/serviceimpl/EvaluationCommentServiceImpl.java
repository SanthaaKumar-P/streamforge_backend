package com.streamforge.serviceimpl;

import com.streamforge.dto.response.EvaluationCommentResponse;
import com.streamforge.entity.Evaluation;
import com.streamforge.entity.EvaluationComment;
import com.streamforge.entity.User;
import com.streamforge.mapper.EvaluationCommentMapper;
import com.streamforge.repository.EvaluationCommentRepository;
import com.streamforge.repository.EvaluationRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.EvaluationCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationCommentServiceImpl implements EvaluationCommentService {


    private final EvaluationCommentRepository commentRepository;

    private final EvaluationRepository evaluationRepository;

    private final UserRepository userRepository;

    private final EvaluationCommentMapper commentMapper;


    @Override
    public EvaluationCommentResponse addComment(
            Long evaluationId,
            Long userId,
            String comment
    ) {


        Evaluation evaluation =
                evaluationRepository.findById(evaluationId)
                .orElseThrow(
                        () -> new RuntimeException("Evaluation not found")
                );


        User user =
                userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );


        EvaluationComment evaluationComment =
                EvaluationComment.builder()
                .evaluation(evaluation)
                .user(user)
                .comment(comment)
                .build();


        return commentMapper.toResponse(
                commentRepository.save(evaluationComment)
        );

    }


    @Override
    public List<EvaluationCommentResponse> getCommentsByEvaluation(
            Long evaluationId
    ) {

        return commentRepository
                .findByEvaluationEvaluationId(evaluationId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();

    }


    @Override
    public void deleteComment(Long commentId) {

        commentRepository.deleteById(commentId);

    }

}