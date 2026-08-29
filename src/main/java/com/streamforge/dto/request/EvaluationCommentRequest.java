package com.streamforge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCommentRequest {

    @NotNull(message = "Evaluation ID is required")
    @Positive(message = "Evaluation ID must be positive")
    private Long evaluationId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotBlank(message = "Comment is required")
    @Size(
            max = 2000,
            message = "Comment cannot exceed 2000 characters"
    )
    private String comment;
}