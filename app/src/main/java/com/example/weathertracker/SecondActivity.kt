package com.example.weathertracker

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val tvParameter = findViewById<TextView>(R.id.tvParameter)
        val btnBack = findViewById<Button>(R.id.btnBack)

        val value = intent.getStringExtra("PARAM_KEY") ?: "не передан"
        tvParameter.text = "Переданный параметр: $value"

        btnBack.setOnClickListener {
            finish()
        }
    }
}