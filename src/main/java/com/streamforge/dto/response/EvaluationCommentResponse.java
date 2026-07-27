package com.streamforge.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationCommentResponse {

    private Long commentId;

    private String comment;

    private Long userId;

    private Long evaluationId;

}