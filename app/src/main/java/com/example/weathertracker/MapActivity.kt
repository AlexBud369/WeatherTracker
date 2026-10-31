package com.example.weathertracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CITY = "extra_map_city"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val cityName = intent.getStringExtra(EXTRA_CITY)?.takeIf { it.isNotBlank() } ?: "Москва"

        val tvMapHeader = findViewById<TextView>(R.id.tvMapHeader)
        val tvMapPinCity = findViewById<TextView>(R.id.tvMapPinCity)
        val btnBackFromMap = findViewById<ImageButton>(R.id.btnBackFromMap)
        val btnCloseMap = findViewById<Button>(R.id.btnCloseMap)
        val btnZoomIn = findViewById<FloatingActionButton>(R.id.btnZoomIn)
        val btnZoomOut = findViewById<FloatingActionButton>(R.id.btnZoomOut)

        tvMapHeader.text = "Интерактивная карта: $cityName"
        tvMapPinCity.text = "$cityName: +18°C, Ясно"

        btnBackFromMap.setOnClickListener {
            // Возврат назад в MainActivity
            finish()
        }

        btnCloseMap.setOnClickListener {
            finish()
        }

        btnZoomIn.setOnClickListener {
            Toast.makeText(this, "Масштаб увеличен (zoom in)", Toast.LENGTH_SHORT).show()
        }

        btnZoomOut.setOnClickListener {
            Toast.makeText(this, "Масштаб уменьшен (zoom out)", Toast.LENGTH_SHORT).show()
        }
    }
}
