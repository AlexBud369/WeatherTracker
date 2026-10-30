package com.example.weathertracker

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvCity = findViewById<TextView>(R.id.tvCity)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvContent = findViewById<TextView>(R.id.tvContent)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Получение переданных через Intent данных (Шаг 3 лабораторной работы)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Отзыв о погоде"
        val city = intent.getStringExtra("EXTRA_CITY") ?: "Город не указан"
        val date = intent.getStringExtra("EXTRA_DATE") ?: "Дата не указана"
        val status = intent.getStringExtra("EXTRA_STATUS") ?: "Совпало"
        val desc = intent.getStringExtra("EXTRA_DESC") ?: "Нет описания"

        tvTitle.text = title
        tvCity.text = "Город: $city"
        tvDate.text = "Дата и время: $date"
        tvStatus.text = "Статус: $status"
        tvContent.text = desc

        // Возврат назад (двунаправленная навигация)
        btnBack.setOnClickListener {
            finish()
        }
    }
}
