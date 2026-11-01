package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WeatherDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CITY = "extra_city"
    }

    private lateinit var tvHeaderCity: TextView
    private lateinit var etDetailCitySearch: EditText
    private lateinit var btnDetailSearch: Button
    private lateinit var layoutHourlyContainer: LinearLayout
    private lateinit var layoutWeeklyContainer: LinearLayout
    private lateinit var btnBack: ImageButton

    private var currentCity: String = "Москва"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_detail)
        bindViews()
        readCityFromIntent()
        setupListeners()
        populateHourlyForecast()
        populateWeeklyForecast()
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_FORECAST)
    }

    private fun bindViews() {
        tvHeaderCity = findViewById(R.id.tvHeaderCity)
        etDetailCitySearch = findViewById(R.id.etDetailCitySearch)
        btnDetailSearch = findViewById(R.id.btnDetailSearch)
        layoutHourlyContainer = findViewById(R.id.layoutHourlyContainer)
        layoutWeeklyContainer = findViewById(R.id.layoutWeeklyContainer)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun readCityFromIntent() {
        currentCity = intent.getStringExtra(EXTRA_CITY)?.takeIf { it.isNotBlank() } ?: "Москва"
        updateCityView(currentCity)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        btnDetailSearch.setOnClickListener {
            val searchedCity = etDetailCitySearch.text.toString().trim()
            if (searchedCity.isNotEmpty()) {
                currentCity = searchedCity
                updateCityView(currentCity)
            }
        }
    }

    private fun updateCityView(cityName: String) {
        tvHeaderCity.text = "Прогноз: $cityName"
        etDetailCitySearch.setText(cityName)
    }

    private fun populateHourlyForecast() {
        layoutHourlyContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)
        for (item in getHourlyData()) {
            val itemView = inflater.inflate(R.layout.item_hourly_weather, layoutHourlyContainer, false)
            itemView.findViewById<TextView>(R.id.tvHourlyTime).text = item.first
            itemView.findViewById<TextView>(R.id.tvHourlyIcon).text = item.second
            itemView.findViewById<TextView>(R.id.tvHourlyTemp).text = item.third
            layoutHourlyContainer.addView(itemView)
        }
    }

    private fun getHourlyData(): List<Triple<String, String, String>> {
        return listOf(
            Triple("10:00", "☀️", "+18°"),
            Triple("12:00", "☀️", "+19°"),
            Triple("14:00", "⛅", "+20°"),
            Triple("16:00", "⛅", "+19°"),
            Triple("18:00", "🌦️", "+17°"),
            Triple("20:00", "🌧️", "+15°"),
            Triple("22:00", "☁️", "+13°"),
            Triple("00:00", "☁️", "+12°"),
            Triple("03:00", "❄️", "+8°"),
            Triple("06:00", "⛅", "+11°"),
            Triple("08:00", "☀️", "+15°")
        )
    }

    private fun populateWeeklyForecast() {
        layoutWeeklyContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)
        for (day in getWeeklyData()) {
            val itemView = inflater.inflate(R.layout.item_weekly_weather, layoutWeeklyContainer, false)
            itemView.findViewById<TextView>(R.id.tvWeeklyDate).text = day.first
            itemView.findViewById<TextView>(R.id.tvWeeklyTemp).text = day.second
            itemView.findViewById<TextView>(R.id.tvWeeklyPrecipitation).text = day.third
            layoutWeeklyContainer.addView(itemView)
        }
    }

    private fun getWeeklyData(): List<Triple<String, String, String>> {
        return listOf(
            Triple("Сегодня, 30 октября", "+18° / +9°", "Осадки: Без осадков ☀️"),
            Triple("Суббота, 31 октября", "+16° / +8°", "Осадки: Переменная облачность ⛅"),
            Triple("Воскресенье, 1 ноября", "+14° / +7°", "Осадки: Небольшой дождь 🌧️"),
            Triple("Понедельник, 2 ноября", "+12° / +5°", "Осадки: Кратковременный дождь 🌦️"),
            Triple("Вторник, 3 ноября", "+13° / +4°", "Осадки: Пасмурно ☁️"),
            Triple("Среда, 4 ноября", "+15° / +6°", "Осадки: Ясно ☀️"),
            Triple("Четверг, 5 ноября", "+10° / +2°", "Осадки: Дождь со снегом ❄️")
        )
    }
}