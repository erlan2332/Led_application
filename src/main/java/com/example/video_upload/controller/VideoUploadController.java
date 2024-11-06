package com.example.video_upload.controller;

import com.example.video_upload.model.Video;
import com.example.video_upload.service.VideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
public class VideoUploadController {

    private final S3Client s3Client;
    private final VideoService videoService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public VideoUploadController(S3Client s3Client, VideoService videoService) {
        this.s3Client = s3Client;
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "rate", required = false, defaultValue = "5") Integer rate,
            @RequestParam("duration") int duration) {
        try {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            String videoUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(uniqueFileName)).toExternalForm();
            LocalDate uploadDate = LocalDate.now();
            LocalDate endDate = uploadDate.plusMonths(duration);

            Video video = new Video(title, rate, duration, uploadDate, endDate, videoUrl);
            videoService.addVideo(video);

            return ResponseEntity.status(HttpStatus.OK).body("Uploaded video: " + title);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload video due to IO error.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload video: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Video>> listVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    @DeleteMapping("/delete/{title}")
    public ResponseEntity<String> deleteVideo(@PathVariable String title) {
        try {
            Video video = videoService.getVideoByTitle(title);
            if (video != null) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(video.getUrl().substring(video.getUrl().lastIndexOf("/") + 1))
                        .build());
                videoService.removeVideo(title);
                return ResponseEntity.ok("Deleted video: " + title);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete video.");
        }
    }

    @GetMapping("/next")
    public ResponseEntity<Video> getNextVideo() {
        Video video = videoService.getNextVideo();
        return video != null ? ResponseEntity.ok(video) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
