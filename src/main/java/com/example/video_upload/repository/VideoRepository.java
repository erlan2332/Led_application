package com.example.video_upload.repository;

import com.example.video_upload.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByTitle(String title);

    @Query("SELECT v.id FROM Video v")  // JPQL-запрос для получения всех ID
    List<Long> findAllIds();
}
