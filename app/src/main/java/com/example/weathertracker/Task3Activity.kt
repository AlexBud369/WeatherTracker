package com.example.weathertracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Task3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task3)

        val reviews = listOf(
            Review("Москва", "12.10.2026 14:00", "Совпало", "Прогноз был точным, солнечно и тепло."),
            Review("Санкт-Петербург", "13.10.2026 09:00", "Не совпало", "Обещали дождь, но было сухо весь день."),
            Review("Казань", "14.10.2026 18:00", "Совпало", "Ветер и облачность как и предсказывали."),
            Review("Новосибирск", "15.10.2026 07:00", "Совпало", "Утром было прохладно, как и обещали.")
        )

        val adapter = ReviewAdapter(reviews) { review ->
            Toast.makeText(
                this,
                "Выбран отзыв: ${review.city}, статус: ${review.status}",
                Toast.LENGTH_SHORT
            ).show()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }
}