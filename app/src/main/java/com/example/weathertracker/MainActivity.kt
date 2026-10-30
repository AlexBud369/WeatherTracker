package com.example.weathertracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val reviews = mutableListOf(
        Review("Москва", "30.10.2026 10:45", "Совпало", "Прогноз был точным, солнечно и тепло."),
        Review("Санкт-Петербург", "29.10.2026 15:30", "Не совпало", "Обещали солнце, но пошел дождь."),
        Review("Казань", "28.10.2026 18:00", "Совпало", "Предупреждение о сильном ветре оправдалось."),
        Review("Новосибирск", "27.10.2026 08:00", "Совпало", "Утром было прохладно, как и предсказывали.")
    )

    private lateinit var adapter: ReviewAdapter
    private lateinit var recyclerView: RecyclerView

    // Шаг 4: регистрация ActivityResultLauncher для получения данных из дочерней Activity
    private val addReviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val city = result.data?.getStringExtra("EXTRA_CITY") ?: "Неизвестный город"
            val date = result.data?.getStringExtra("EXTRA_DATE") ?: "30.10.2026 12:00"
            val status = result.data?.getStringExtra("EXTRA_STATUS") ?: "Совпало"
            val desc = result.data?.getStringExtra("EXTRA_DESC") ?: ""

            // Добавляем новый отзыв в список и уведомляем адаптер
            reviews.add(0, Review(city, date, status, desc))
            adapter.notifyItemInserted(0)
            recyclerView.scrollToPosition(0)

            Toast.makeText(this, "Отзыв успешно добавлен: $city ($status)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        val btnAdd = findViewById<Button>(R.id.btnAdd)

        // Шаг 3: при клике на элемент списка передаем данные в DetailActivity через Intent
        adapter = ReviewAdapter(reviews) { review ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_TITLE", "Отзыв: ${review.city}")
                putExtra("EXTRA_CITY", review.city)
                putExtra("EXTRA_DATE", review.date)
                putExtra("EXTRA_STATUS", review.status)
                putExtra("EXTRA_DESC", review.comment)
            }
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Шаги 2 и 4: переход на экран добавления отзыва по кнопке btnAdd
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            addReviewLauncher.launch(intent)
        }
    }
}
