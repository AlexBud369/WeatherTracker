package com.example.weathertracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CITY = "extra_map_city"
    }

    private lateinit var tvMapHeader: TextView
    private lateinit var tvMapPinCity: TextView
    private lateinit var btnBackFromMap: ImageButton
    private lateinit var btnCloseMap: Button
    private lateinit var btnZoomIn: FloatingActionButton
    private lateinit var btnZoomOut: FloatingActionButton

    private var zoomLevel: Int = 10
    private var cityName: String = "Москва"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        bindViews()
        loadCity()
        setupButtons()
    }

    private fun bindViews() {
        tvMapHeader = findViewById(R.id.tvMapHeader)
        tvMapPinCity = findViewById(R.id.tvMapPinCity)
        btnBackFromMap = findViewById(R.id.btnBackFromMap)
        btnCloseMap = findViewById(R.id.btnCloseMap)
        btnZoomIn = findViewById(R.id.btnZoomIn)
        btnZoomOut = findViewById(R.id.btnZoomOut)
    }

    private fun loadCity() {
        cityName = intent.getStringExtra(EXTRA_CITY)?.takeIf { it.isNotBlank() } ?: "Москва"
        tvMapHeader.text = "Интерактивная карта: $cityName"
        updatePinText()
    }

    private fun setupButtons() {
        btnBackFromMap.setOnClickListener { finish() }
        btnCloseMap.setOnClickListener { finish() }
        btnZoomIn.setOnClickListener { zoomIn() }
        btnZoomOut.setOnClickListener { zoomOut() }
    }

    private fun zoomIn() {
        if (zoomLevel < 18) {
            zoomLevel++
            updatePinText()
        }
    }

    private fun zoomOut() {
        if (zoomLevel > 3) {
            zoomLevel--
            updatePinText()
        }
    }

    private fun updatePinText() {
        tvMapPinCity.text = "$cityName: +18°C, Масштаб ${zoomLevel}x"
    }
}