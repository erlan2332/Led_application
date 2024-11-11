package com.example.video_upload.service;

import com.example.video_upload.model.Video;
import com.example.video_upload.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final Queue<Video> videoQueue = new LinkedList<>();

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
        loadVideoQueue();
    }

    private void loadVideoQueue() {
        List<Video> allVideos = videoRepository.findAll();
        videoQueue.clear();

        for (Video video : allVideos) {
            if (LocalDate.now().isBefore(video.getEndDate())) {
                videoQueue.offer(video);
            }
        }
    }

    public Video getNextVideo() {
        LocalDate now = LocalDate.now();

        for (Video video : videoQueue) {
            if (now.isBefore(video.getEndDate())) {
                return video;
            }
        }
        return null;
    }

    @Scheduled(fixedRate = 600000)
    public void removeExpiredVideos() {
        LocalDate now = LocalDate.now();
        List<Video> expiredVideos = new ArrayList<>();

        for (Video video : videoQueue) {
            if (now.isAfter(video.getEndDate())) {
                expiredVideos.add(video);
            }
        }

        for (Video video : expiredVideos) {
            deleteVideo(video);
        }
    }

    private void deleteVideo(Video video) {
        videoRepository.delete(video);
        String fileKey = video.getUrl().substring(video.getUrl().lastIndexOf("/") + 1);
        System.out.println("Удалено видео: " + video.getTitle());
    }

    public Video createAndSaveVideo(String title, int duration, boolean isMinutes, String videoUrl) {
        LocalDate uploadDate = LocalDate.now();
        LocalDateTime startOfDay = uploadDate.atStartOfDay();
        LocalDateTime endDate = isMinutes
                ? startOfDay.plusMinutes(duration)
                : startOfDay.plusMonths(duration);

        Long newId = findAvailableId(); // Поиск наименьшего доступного ID

        Video video = new Video(newId, title, duration, uploadDate, endDate.toLocalDate(), videoUrl);
        return videoRepository.save(video);
    }

    // Метод для поиска наименьшего доступного ID
    private Long findAvailableId() {
        List<Long> usedIds = videoRepository.findAllIds(); // Получаем все существующие ID
        Long id = 1L;
        while (usedIds.contains(id)) {
            id++;
        }
        return id; // Возвращаем наименьший доступный ID
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id).orElse(null);
    }

    public void removeVideo(Long id) {
        videoRepository.findById(id).ifPresent(videoRepository::delete);
    }
}
