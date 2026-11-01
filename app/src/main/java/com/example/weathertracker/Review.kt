package com.example.weathertracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val city: String,
    val date: String,
    val status: String,
    val comment: String,
    val title: String = "Отзыв о погоде"
) : Serializable
