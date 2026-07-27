package com.streamforge.dto.response;

import com.streamforge.enums.EpisodeStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EpisodeResponse {

    private Long episodeId;

    private Integer episodeNumber;

    private String title;

    private Integer durationMinutes;

    private String description;

    private EpisodeStatus status;

}