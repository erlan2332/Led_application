package com.example.video_upload.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Video {

    @Id
    private Long id; // Убираем @GeneratedValue для ручного назначения ID

    private String title;  // Название видео
    private int duration;  // Продолжительность показа в месяцах или минутах (зависит от флага)
    private LocalDate uploadDate; // Дата загрузки
    private LocalDate endDate;    // Дата окончания показа
    private String url;    // URL видео в S3

    // Пустой конструктор, необходим для JPA
    public Video() {}

    // Конструктор для инициализации всех полей, включая ID
    public Video(Long id, String title, int duration, LocalDate uploadDate, LocalDate endDate, String url) {
        this.id = id;
        this.title = title;
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

    // Переопределённый метод toString для удобства отображения объекта в логах и отладке
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
