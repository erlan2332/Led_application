package com.example.video_upload.service;

import com.example.video_upload.model.Video;
import com.example.video_upload.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private List<Video> videoQueue;
    private Map<Long, Integer> videoShowCount;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
        loadVideoQueue();
    }

    private void loadVideoQueue() {
        videoQueue = videoRepository.findAll();
        videoShowCount = new HashMap<>();
        for (Video video : videoQueue) {
            videoShowCount.put(video.getId(), 0);
        }
    }

    public Video getNextVideo() {
        return videoQueue.stream()
                .filter(video -> videoShowCount.get(video.getId()) < video.getRate()
                        && LocalDate.now().isBefore(video.getEndDate()))
                .findFirst()
                .map(video -> {
                    videoShowCount.put(video.getId(), videoShowCount.get(video.getId()) + 1);
                    return video;
                }).orElse(null);
    }

    public void addVideo(Video video) {
        videoRepository.save(video);
        loadVideoQueue();
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoByTitle(String title) {
        return videoRepository.findByTitle(title).orElse(null);
    }

    public void removeVideo(String title) {
        videoRepository.findByTitle(title).ifPresent(video -> {
            videoRepository.delete(video);
            loadVideoQueue();
        });
    }

    @Scheduled(fixedRate = 3600000)
    public void resetShowCounts() {
        videoShowCount.replaceAll((id, count) -> 0);
    }
}
