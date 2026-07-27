package com.streamforge.repository;

import com.streamforge.entity.Show;
import com.streamforge.enums.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByStatus(ShowStatus status);

    List<Show> findByCreatorUserId(Long userId);

    List<Show> findByLanguage(String language);

    List<Show> findByTitleContainingIgnoreCase(String title);

}