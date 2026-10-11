package com.example.weathertracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etParameter = findViewById<EditText>(R.id.etParameter)
        val btn1 = findViewById<Button>(R.id.btn1)

        btn1.setOnClickListener {
            val value = etParameter.text.toString().trim()

            if (value.isEmpty()) {
                etParameter.error = "Введите параметр"
                return@setOnClickListener
            }

            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("PARAM_KEY", value)
            startActivity(intent)
        }
    }
}