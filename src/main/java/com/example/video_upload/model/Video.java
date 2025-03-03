package com.example.video_upload.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Video {

    @Id
    private Long id;

    private String title;
    private int duration;
    private LocalDate uploadDate;
    private LocalDate endDate;   
    private String url;    

    public Video() {}

    public Video(Long id, String title, int duration, LocalDate uploadDate, LocalDate endDate, String url) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.uploadDate = uploadDate;
        this.endDate = endDate;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", uploadDate=" + uploadDate +
                ", endDate=" + endDate +
                ", url='" + url + '\'' +
                '}';
    }
}
