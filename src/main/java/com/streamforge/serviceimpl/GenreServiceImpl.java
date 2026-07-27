package com.streamforge.serviceimpl;

import com.streamforge.dto.request.GenreRequest;
import com.streamforge.dto.response.GenreResponse;
import com.streamforge.entity.Genre;
import com.streamforge.mapper.GenreMapper;
import com.streamforge.repository.GenreRepository;
import com.streamforge.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public GenreResponse createGenre(GenreRequest request) {

        Genre genre = genreMapper.toEntity(request);

        return genreMapper.toResponse(
                genreRepository.save(genre)
        );
    }

    @Override
    public GenreResponse getGenreById(Long genreId) {

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        return genreMapper.toResponse(genre);
    }

    @Override
    public List<GenreResponse> getAllGenres() {

        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toResponse)
                .toList();
    }

    @Override
    public GenreResponse updateGenre(
            Long genreId,
            GenreRequest request
    ) {

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        genre.setGenreName(request.getGenreName());
        genre.setDescription(request.getDescription());

        return genreMapper.toResponse(
                genreRepository.save(genre)
        );
    }

    @Override
    public void deleteGenre(Long genreId) {

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        genreRepository.delete(genre);
    }

}