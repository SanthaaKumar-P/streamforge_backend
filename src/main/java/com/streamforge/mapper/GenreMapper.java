package com.streamforge.mapper;

import com.streamforge.dto.request.GenreRequest;
import com.streamforge.dto.response.GenreResponse;
import com.streamforge.entity.Genre;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    private final ModelMapper modelMapper;

    public GenreMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Genre toEntity(GenreRequest request) {
        return modelMapper.map(request, Genre.class);
    }

    public GenreResponse toResponse(Genre genre) {
        return modelMapper.map(genre, GenreResponse.class);
    }

}