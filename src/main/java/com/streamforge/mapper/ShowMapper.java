package com.streamforge.mapper;

import com.streamforge.dto.response.ShowResponse;
import com.streamforge.entity.Show;
import org.springframework.stereotype.Component;

@Component
public class ShowMapper {


    private final UserMapper userMapper;


    public ShowMapper(UserMapper userMapper){
        this.userMapper=userMapper;
    }


    public ShowResponse toResponse(Show show){

        return ShowResponse.builder()
                .showId(show.getShowId())
                .title(show.getTitle())
                .description(show.getDescription())
                .synopsis(show.getSynopsis())
                .language(show.getLanguage())
                .targetAudience(show.getTargetAudience())
                .estimatedBudget(show.getEstimatedBudget())
                .expectedReleaseDate(show.getExpectedReleaseDate())
                .status(show.getStatus())
                .creator(
                    show.getCreator()!=null ?
                    userMapper.toResponse(show.getCreator())
                    : null
                )
                .build();

    }

}