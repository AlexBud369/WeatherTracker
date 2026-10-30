package com.example.weathertracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class AddReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        val etCity = findViewById<EditText>(R.id.etCity)
        val etDate = findViewById<EditText>(R.id.etDate)
        val rbMatched = findViewById<RadioButton>(R.id.rbMatched)
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // Возврат данных в MainActivity через ActivityResultLauncher (Шаг 4 лабораторной работы)
        btnSave.setOnClickListener {
            val city = etCity.text.toString().trim()
            val date = etDate.text.toString().trim().ifEmpty { "30.10.2026 12:00" }
            val status = if (rbMatched.isChecked) "Совпало" else "Не совпало"
            val comment = etComment.text.toString().trim()

            if (city.isEmpty()) {
                etCity.error = "Введите город"
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("EXTRA_TITLE", "Отзыв: $city")
                putExtra("EXTRA_CITY", city)
                putExtra("EXTRA_DATE", date)
                putExtra("EXTRA_STATUS", status)
                putExtra("EXTRA_DESC", comment)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
