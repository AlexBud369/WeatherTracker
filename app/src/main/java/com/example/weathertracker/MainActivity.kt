package com.example.weathertracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weathertracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn1.setOnClickListener {
            val value = binding.etParameter.text.toString().trim()

            if (value.isEmpty()) {
                binding.etParameter.error = "Введите параметр"
                return@setOnClickListener
            }

            binding.tvResult.text = "Вы ищете погоду в городе: $value"

        }

        binding.btnClear.setOnClickListener {
            binding.etParameter.text.clear()
            binding.tvResult.text = "Результат появится здесь"
            binding.etParameter.error = null
        }
    }
}