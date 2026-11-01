package com.example.weathertracker

import java.io.Serializable
import java.util.concurrent.atomic.AtomicLong

data class Review(
    val city: String,
    val date: String,
    val status: String,
    val comment: String,
    val title: String = "Отзыв о погоде",
    val id: Long = idGenerator.incrementAndGet()
) : Serializable {
    companion object {
        private val idGenerator = AtomicLong(1000L)
    }
}