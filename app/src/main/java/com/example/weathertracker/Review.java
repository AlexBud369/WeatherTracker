package com.example.weathertracker;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "reviews")
public class Review implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String city;
    private String date;
    private String status;
    private String comment;
    private String title;

    @Ignore
    public Review() {
        this.title = "Отзыв о погоде";
    }

    public Review(long id, String city, String date, String status, String comment, String title) {
        this.id = id;
        this.city = city;
        this.date = date;
        this.status = status;
        this.comment = comment;
        this.title = (title != null && !title.isEmpty()) ? title : "Отзыв о погоде";
    }

    @Ignore
    public Review(String city, String date, String status, String comment, String title) {
        this(0L, city, date, status, comment, title);
    }

    public Review copy(String title, String city, String date, String status, String comment) {
        return new Review(this.id, city, date, status, comment, title);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
