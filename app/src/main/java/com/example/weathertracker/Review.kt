package com.example.weathertracker

import java.io.Serializable

data class Review(
    val city: String,
    val date: String,
    val status: String,
    val comment: String,
    val title: String = "Отзыв о погоде",
    val id: Long = System.currentTimeMillis()
) : Serializable
