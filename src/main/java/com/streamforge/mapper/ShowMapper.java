package com.streamforge.mapper;

import com.streamforge.dto.response.GenreResponse;
import com.streamforge.dto.response.ShowResponse;
import com.streamforge.entity.Show;
import com.streamforge.entity.ShowGenre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShowMapper {

    private final UserMapper userMapper;

    public ShowMapper(
            UserMapper userMapper
    ) {
        this.userMapper = userMapper;
    }

    public ShowResponse toResponse(
            Show show
    ) {

        List<GenreResponse> genres =
                show.getShowGenres()
                        .stream()
                        .map(
                                ShowGenre::getGenre
                        )
                        .map(
                                genre ->
                                        GenreResponse.builder()
                                                .genreId(
                                                        genre.getGenreId()
                                                )
                                                .genreName(
                                                        genre.getGenreName()
                                                )
                                                .description(
                                                        genre.getDescription()
                                                )
                                                .build()
                        )
                        .toList();

        return ShowResponse.builder()
                .showId(
                        show.getShowId()
                )
                .title(
                        show.getTitle()
                )
                .description(
                        show.getDescription()
                )
                .synopsis(
                        show.getSynopsis()
                )
                .language(
                        show.getLanguage()
                )
                .targetAudience(
                        show.getTargetAudience()
                )
                .episodeCount(
                        show.getEpisodeCount()
                )
                .estimatedBudget(
                        show.getEstimatedBudget()
                )
                .expectedReleaseDate(
                        show.getExpectedReleaseDate()
                )
                .status(
                        show.getStatus()
                )
                .creator(
                        show.getCreator() != null
                                ? userMapper.toResponse(
                                        show.getCreator()
                                )
                                : null
                )
                .genres(
                        genres
                )
                .build();
    }
}