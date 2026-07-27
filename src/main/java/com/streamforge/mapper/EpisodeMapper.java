package com.streamforge.mapper;

import com.streamforge.dto.response.EpisodeResponse;
import com.streamforge.entity.Episode;
import org.springframework.stereotype.Component;

@Component
public class EpisodeMapper {

    public EpisodeResponse toResponse(Episode episode){

        return EpisodeResponse.builder()
                .episodeId(episode.getEpisodeId())
                .episodeNumber(episode.getEpisodeNumber())
                .title(episode.getTitle())
                .durationMinutes(episode.getDurationMinutes())
                .description(episode.getDescription())
                .status(episode.getStatus())
                .build();

    }

}