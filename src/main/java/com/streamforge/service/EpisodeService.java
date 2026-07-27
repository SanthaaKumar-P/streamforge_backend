package com.streamforge.service;

import com.streamforge.dto.request.EpisodeRequest;
import com.streamforge.dto.response.EpisodeResponse;

import java.util.List;

public interface EpisodeService {

    EpisodeResponse createEpisode(EpisodeRequest request);

    List<EpisodeResponse> getEpisodesByShow(Long showId);

    EpisodeResponse getEpisodeById(Long episodeId);

    EpisodeResponse updateEpisode(Long episodeId, EpisodeRequest request);

    void deleteEpisode(Long episodeId);

}