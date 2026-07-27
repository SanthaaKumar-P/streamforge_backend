package com.streamforge.repository;

import com.streamforge.entity.ShowGenre;
import com.streamforge.entity.ShowGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowGenreRepository 
        extends JpaRepository<ShowGenre, ShowGenreId> {

}