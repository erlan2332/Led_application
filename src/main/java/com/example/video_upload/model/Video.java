package com.example.video_upload.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;  // Название видео
    private int rate;      // Количество показов в час
    private int duration;  // Продолжительность в месяцах
    private LocalDate uploadDate; // Дата загрузки
    private LocalDate endDate;    // Дата окончания показа
    private String url;    // URL видео в S3

    // Пустой конструктор
    public Video() {}

    // Конструктор для инициализации всех полей
    public Video(String title, int rate, int duration, LocalDate uploadDate, LocalDate endDate, String url) {
        this.title = title;
        this.rate = rate;
        this.duration = duration;
        this.uploadDate = uploadDate;
        this.endDate = endDate;
        this.url = url;
    }

    // Геттеры и сеттеры для всех полей
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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
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
                ", rate=" + rate +
                ", duration=" + duration +
                ", uploadDate=" + uploadDate +
                ", endDate=" + endDate +
                ", url='" + url + '\'' +
                '}';
    }
}
