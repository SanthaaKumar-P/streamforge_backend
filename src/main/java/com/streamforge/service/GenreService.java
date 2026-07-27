package com.streamforge.service;

import com.streamforge.dto.request.GenreRequest;
import com.streamforge.dto.response.GenreResponse;

import java.util.List;

public interface GenreService {

    GenreResponse createGenre(GenreRequest request);

    GenreResponse getGenreById(Long genreId);

    List<GenreResponse> getAllGenres();

    GenreResponse updateGenre(Long genreId, GenreRequest request);

    void deleteGenre(Long genreId);

}