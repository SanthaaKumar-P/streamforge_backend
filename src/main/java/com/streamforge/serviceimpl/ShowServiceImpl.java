package com.streamforge.serviceimpl;

import com.streamforge.dto.request.ShowRequest;
import com.streamforge.dto.response.ShowResponse;
import com.streamforge.entity.Show;
import com.streamforge.entity.User;
import com.streamforge.exception.ResourceNotFoundException;
import com.streamforge.mapper.ShowMapper;
import com.streamforge.repository.ShowRepository;
import com.streamforge.repository.UserRepository;
import com.streamforge.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final ShowMapper showMapper;

    @Override
    public ShowResponse createShow(ShowRequest request) {

        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Creator not found with id: " + request.getCreatorId()
                        )
                );

        Show show = Show.builder()
                .creator(creator)
                .title(request.getTitle())
                .description(request.getDescription())
                .synopsis(request.getSynopsis())
                .language(request.getLanguage())
                .targetAudience(request.getTargetAudience())
                .estimatedBudget(request.getEstimatedBudget())
                .expectedReleaseDate(request.getExpectedReleaseDate())
                .status(request.getStatus())
                .build();

        return showMapper.toResponse(
                showRepository.save(show)
        );
    }

    @Override
    public ShowResponse getShowById(Long showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: " + showId
                        )
                );

        return showMapper.toResponse(show);
    }

    @Override
    public List<ShowResponse> getAllShows() {

        return showRepository.findAll()
                .stream()
                .map(showMapper::toResponse)
                .toList();
    }

    @Override
    public ShowResponse updateShow(
            Long showId,
            ShowRequest request
    ) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: " + showId
                        )
                );

        show.setTitle(request.getTitle());
        show.setDescription(request.getDescription());
        show.setSynopsis(request.getSynopsis());
        show.setLanguage(request.getLanguage());
        show.setTargetAudience(request.getTargetAudience());
        show.setEstimatedBudget(request.getEstimatedBudget());
        show.setExpectedReleaseDate(request.getExpectedReleaseDate());
        show.setStatus(request.getStatus());

        return showMapper.toResponse(
                showRepository.save(show)
        );
    }

    @Override
    public void deleteShow(Long showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Show not found with id: " + showId
                        )
                );

        showRepository.delete(show);
    }
}