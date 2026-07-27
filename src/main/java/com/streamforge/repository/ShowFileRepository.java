package com.streamforge.repository;

import com.streamforge.entity.ShowFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowFileRepository extends JpaRepository<ShowFile, Long> {

    List<ShowFile> findByShowShowId(Long showId);

}