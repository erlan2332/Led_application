package com.example.video_upload.controller;

import com.example.video_upload.model.Video;
import com.example.video_upload.service.VideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
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
            @RequestParam("duration") int duration,
            @RequestParam(value = "isMinutes", defaultValue = "false") boolean isMinutes) {

        if (file.isEmpty() || title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid file or title input.");
        }

        try {
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(uniqueFileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );

            String videoUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(uniqueFileName)).toExternalForm();
            Video savedVideo = videoService.createAndSaveVideo(title, duration, isMinutes, videoUrl);

            return ResponseEntity.ok("Uploaded video with ID: " + savedVideo.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload error: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Video>> listVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long id) {
        try {
            Video video = videoService.getVideoById(id);
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found with ID: " + id);
            }

            String fileKey = video.getUrl().substring(video.getUrl().lastIndexOf("/") + 1);
            deleteVideoFromS3(fileKey);
            videoService.removeVideo(id);

            return ResponseEntity.ok("Deleted video with ID: " + id);
        } catch (RuntimeException e) {
            System.err.println("Deletion error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete video: " + e.getMessage());
        }
    }

    private void deleteVideoFromS3(String fileKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileKey).build());
        } catch (Exception e) {
            System.err.println("Error deleting from S3: " + e.getMessage());
            throw new RuntimeException("Failed to delete video from S3", e);
        }
    }
}
