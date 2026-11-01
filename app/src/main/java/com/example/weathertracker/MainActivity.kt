package com.example.weathertracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

    private val addReviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val title = result.data?.getStringExtra(AddReviewActivity.EXTRA_TITLE) ?: ""
            val city = result.data?.getStringExtra(AddReviewActivity.EXTRA_CITY) ?: currentCity
            val status = result.data?.getStringExtra(AddReviewActivity.EXTRA_STATUS) ?: "Совпало"
            displayNotice(title, city, status)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        setupSearch()
        setupNavigationButtons()
        updateWeatherDisplay(currentCity)
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_WEATHER)
    }

    private fun bindViews() {
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
    }

    private fun setupSearch() {
        btnSearch.setOnClickListener {
            val query = etSearchCity.text.toString().trim()
            if (query.isNotEmpty()) {
                currentCity = query
                updateWeatherDisplay(currentCity)
            }
        }
    }

    private fun setupNavigationButtons() {
        btnShowDetails.setOnClickListener {
            val intent = Intent(this, WeatherDetailActivity::class.java).apply {
                putExtra(WeatherDetailActivity.EXTRA_CITY, currentCity)
            }
            startActivity(intent)
        }
        btnAddReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java).apply {
                putExtra(AddReviewActivity.EXTRA_CITY, currentCity)
            }
            addReviewLauncher.launch(intent)
        }
        cardMapPreview.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java).apply {
                putExtra(MapActivity.EXTRA_CITY, currentCity)
            }
            startActivity(intent)
        }
    }

    private fun displayNotice(title: String, city: String, status: String) {
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
            "санкт-петербург", "питер" -> setWeatherData("+11°C", "Ощущается как +8°C", "Дождь 🌧️", "6.5 м/с", "88%", "747 мм")
            "сочи" -> setWeatherData("+22°C", "Ощущается как +23°C", "Солнечно ☀️", "2.1 м/с", "55%", "758 мм")
            "новосибирск" -> setWeatherData("+4°C", "Ощущается как +1°C", "Облачно ☁️", "5.0 м/с", "72%", "750 мм")
            else -> setWeatherData("+18°C", "Ощущается как +16°C", "Ясно ☀️", "4.2 м/с", "65%", "752 мм")
        }
    }

    private fun setWeatherData(temp: String, feels: String, cond: String, wind: String, hum: String, press: String) {
        tvCurrentTemp.text = temp
        tvFeelsLike.text = feels
        tvCondition.text = cond
        tvWind.text = wind
        tvHumidity.text = hum
        tvPressure.text = press
    }
}