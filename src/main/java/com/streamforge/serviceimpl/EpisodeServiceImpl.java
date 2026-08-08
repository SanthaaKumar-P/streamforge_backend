package com.streamforge.serviceimpl;

import com.streamforge.dto.request.EpisodeRequest;
import com.streamforge.dto.response.EpisodeResponse;
import com.streamforge.entity.Episode;
import com.streamforge.entity.Show;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.EpisodeMapper;
import com.streamforge.repository.EpisodeRepository;
import com.streamforge.repository.ShowRepository;
import com.streamforge.service.EpisodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final ShowRepository showRepository;
    private final EpisodeMapper episodeMapper;

    @Override
    public EpisodeResponse createEpisode(EpisodeRequest request) {

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: " + request.getShowId()
                        )
                );

        Episode episode = Episode.builder()
                .show(show)
                .episodeNumber(request.getEpisodeNumber())
                .title(request.getTitle())
                .durationMinutes(request.getDurationMinutes())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();

        return episodeMapper.toResponse(
                episodeRepository.save(episode)
        );
    }

    @Override
    public List<EpisodeResponse> getEpisodesByShow(Long showId) {

        // Verify that the show exists first
        if (!showRepository.existsById(showId)) {
            throw new ResourceNotFoundException(
                    "Show not found with id: " + showId
            );
        }

        return episodeRepository.findByShowShowId(showId)
                .stream()
                .map(episodeMapper::toResponse)
                .toList();
    }

    @Override
    public EpisodeResponse getEpisodeById(Long episodeId) {

        return episodeRepository.findById(episodeId)
                .map(episodeMapper::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Episode not found with id: " + episodeId
                        )
                );
    }

    @Override
    public EpisodeResponse updateEpisode(
            Long episodeId,
            EpisodeRequest request
    ) {

        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Episode not found with id: " + episodeId
                        )
                );

        episode.setTitle(request.getTitle());
        episode.setDurationMinutes(request.getDurationMinutes());
        episode.setDescription(request.getDescription());
        episode.setStatus(request.getStatus());

        return episodeMapper.toResponse(
                episodeRepository.save(episode)
        );
    }

    @Override
    public void deleteEpisode(Long episodeId) {

        Episode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Episode not found with id: " + episodeId
                        )
                );

        episodeRepository.delete(episode);
    }
}