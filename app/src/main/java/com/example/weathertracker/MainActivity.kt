package com.example.weathertracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var etSearchCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvCityName: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var tvCurrentTemp: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var cardMapPreview: MaterialCardView
    private lateinit var tvMapCityTitle: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvPressure: TextView
    private lateinit var btnShowDetails: Button
    private lateinit var btnAddReview: Button
    private lateinit var tvLatestReviewNotice: TextView

    private var currentCity: String = "Москва"

    private val hideNoticeRunnable = Runnable {
        tvLatestReviewNotice.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                tvLatestReviewNotice.visibility = View.GONE
                tvLatestReviewNotice.alpha = 1f
            }
    }

    // Регистрация ActivityResultLauncher для получения возвращаемых данных (Лаб 8, Шаг 4)
    private val addReviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra(AddReviewActivity.EXTRA_TITLE) ?: ""
            val city = data?.getStringExtra(AddReviewActivity.EXTRA_CITY) ?: currentCity
            val status = data?.getStringExtra(AddReviewActivity.EXTRA_STATUS) ?: "Совпало"

            tvLatestReviewNotice.apply {
                removeCallbacks(hideNoticeRunnable)
                alpha = 1f
                visibility = View.VISIBLE
                text = "✓ Добавлен отзыв: «$title» для города $city ($status)  ✕"
                setOnClickListener {
                    removeCallbacks(hideNoticeRunnable)
                    visibility = View.GONE
                }
                postDelayed(hideNoticeRunnable, 4000)
            }

            Toast.makeText(
                this,
                "Результат успешно получен из дочерней Activity: «$title»",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearchCity = findViewById(R.id.etSearchCity)
        btnSearch = findViewById(R.id.btnSearch)
        tvCityName = findViewById(R.id.tvCityName)
        tvCondition = findViewById(R.id.tvCondition)
        tvDateTime = findViewById(R.id.tvDateTime)
        tvCurrentTemp = findViewById(R.id.tvCurrentTemp)
        tvFeelsLike = findViewById(R.id.tvFeelsLike)
        cardMapPreview = findViewById(R.id.cardMapPreview)
        tvMapCityTitle = findViewById(R.id.tvMapCityTitle)
        tvWind = findViewById(R.id.tvWind)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvPressure = findViewById(R.id.tvPressure)
        btnShowDetails = findViewById(R.id.btnShowDetails)
        btnAddReview = findViewById(R.id.btnAddReview)
        tvLatestReviewNotice = findViewById(R.id.tvLatestReviewNotice)

        updateWeatherDisplay(currentCity)

        // Поиск города
        btnSearch.setOnClickListener {
            val query = etSearchCity.text.toString().trim()
            if (query.isNotEmpty()) {
                currentCity = query
                updateWeatherDisplay(currentCity)
                Toast.makeText(this, "Город изменен на: $currentCity", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Введите название города для поиска", Toast.LENGTH_SHORT).show()
            }
        }

        // Переход на экран подробного прогноза с передачей города через Intent (Шаг 2 и 3 Лаб 8)
        btnShowDetails.setOnClickListener {
            val intent = Intent(this, WeatherDetailActivity::class.java).apply {
                putExtra(WeatherDetailActivity.EXTRA_CITY, currentCity)
            }
            startActivity(intent)
        }

        // Открытие экрана добавления отзыва с ожиданием результата (Шаг 4 Лаб 8)
        btnAddReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java).apply {
                putExtra(AddReviewActivity.EXTRA_CITY, currentCity)
            }
            addReviewLauncher.launch(intent)
        }

        // Открытие увеличенной карты поверх экрана через явный Intent
        cardMapPreview.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java).apply {
                putExtra(MapActivity.EXTRA_CITY, currentCity)
            }
            startActivity(intent)
        }

        // Инициализация нижней панели навигации
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_WEATHER)
    }

    override fun onResume() {
        super.onResume()
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_WEATHER)
    }

    override fun onDestroy() {
        super.onDestroy()
        tvLatestReviewNotice.removeCallbacks(hideNoticeRunnable)
    }

    private fun updateWeatherDisplay(city: String) {
        tvCityName.text = city
        tvMapCityTitle.text = "Интерактивная карта осадков: $city"
        when (city.lowercase()) {
            "санкт-петербург", "питер" -> {
                tvCurrentTemp.text = "+11°C"
                tvFeelsLike.text = "Ощущается как +8°C"
                tvCondition.text = "Дождь 🌧️"
                tvWind.text = "6.5 м/с"
                tvHumidity.text = "88%"
                tvPressure.text = "747 мм"
            }
            "сочи" -> {
                tvCurrentTemp.text = "+22°C"
                tvFeelsLike.text = "Ощущается как +23°C"
                tvCondition.text = "Солнечно ☀️"
                tvWind.text = "2.1 м/с"
                tvHumidity.text = "55%"
                tvPressure.text = "758 мм"
            }
            "новосибирск" -> {
                tvCurrentTemp.text = "+4°C"
                tvFeelsLike.text = "Ощущается как +1°C"
                tvCondition.text = "Облачно ☁️"
                tvWind.text = "5.0 м/с"
                tvHumidity.text = "72%"
                tvPressure.text = "750 мм"
            }
            else -> {
                tvCurrentTemp.text = "+18°C"
                tvFeelsLike.text = "Ощущается как +16°C"
                tvCondition.text = "Ясно ☀️"
                tvWind.text = "4.2 м/с"
                tvHumidity.text = "65%"
                tvPressure.text = "752 мм"
            }
        }
    }
}
