package com.example.video_upload.repository;

import com.example.video_upload.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByTitle(String title);
}
