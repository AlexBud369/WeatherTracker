package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ForecastFragment : Fragment() {

    companion object {
        private const val ARG_CITY = "arg_city"

        fun newInstance(city: String = "Москва"): ForecastFragment {
            val fragment = ForecastFragment()
            val args = Bundle().apply {
                putString(ARG_CITY, city)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var tvHeaderCity: TextView
    private lateinit var etDetailCitySearch: EditText
    private lateinit var btnDetailSearch: Button
    private lateinit var layoutHourlyContainer: LinearLayout
    private lateinit var layoutWeeklyContainer: LinearLayout
    private lateinit var btnBack: ImageButton

    private var currentCity: String = "Москва"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvHeaderCity = view.findViewById(R.id.tvHeaderCity)
        etDetailCitySearch = view.findViewById(R.id.etDetailCitySearch)
        btnDetailSearch = view.findViewById(R.id.btnDetailSearch)
        layoutHourlyContainer = view.findViewById(R.id.layoutHourlyContainer)
        layoutWeeklyContainer = view.findViewById(R.id.layoutWeeklyContainer)
        btnBack = view.findViewById(R.id.btnBack)

        // Получение переданного аргумента из Bundle (Шаг 5 Лаб 9)
        currentCity = arguments?.getString(ARG_CITY)?.takeIf { it.isNotBlank() } ?: "Москва"
        updateCityView(currentCity)

        // Возврат назад по стеку фрагментов (BackStack, Шаг 4 Лаб 9)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnDetailSearch.setOnClickListener {
            val searchedCity = etDetailCitySearch.text.toString().trim()
            if (searchedCity.isNotEmpty()) {
                currentCity = searchedCity
                updateCityView(currentCity)
                Toast.makeText(requireContext(), "Загружен прогноз для: $currentCity", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Введите название города", Toast.LENGTH_SHORT).show()
            }
        }

        populateHourlyForecast()
        populateWeeklyForecast()
    }

    private fun updateCityView(cityName: String) {
        tvHeaderCity.text = "Прогноз: $cityName"
        etDetailCitySearch.setText(cityName)
    }

    private fun populateHourlyForecast() {
        layoutHourlyContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        val hourlyList = listOf(
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

        for (item in hourlyList) {
            val itemView = inflater.inflate(R.layout.item_hourly_weather, layoutHourlyContainer, false)
            itemView.findViewById<TextView>(R.id.tvHourlyTime).text = item.first
            itemView.findViewById<TextView>(R.id.tvHourlyIcon).text = item.second
            itemView.findViewById<TextView>(R.id.tvHourlyTemp).text = item.third
            layoutHourlyContainer.addView(itemView)
        }
    }

    private fun populateWeeklyForecast() {
        layoutWeeklyContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        data class DayForecast(
            val date: String,
            val temp: String,
            val precipitation: String,
            val wind: String,
            val humidity: String,
            val pressure: String
        )

        val days = listOf(
            DayForecast("Сегодня, 30 октября", "+18° / +9°", "Осадки: Без осадков ☀️", "💨 3.8 м/с", "💧 58%", "⏱ 754 мм"),
            DayForecast("Суббота, 31 октября", "+16° / +8°", "Осадки: Переменная облачность ⛅", "💨 4.2 м/с", "💧 62%", "⏱ 752 мм"),
            DayForecast("Воскресенье, 1 ноября", "+14° / +7°", "Осадки: Небольшой дождь 🌧️", "💨 5.1 м/с", "💧 75%", "⏱ 748 мм"),
            DayForecast("Понедельник, 2 ноября", "+12° / +5°", "Осадки: Кратковременный дождь 🌦️", "💨 4.0 м/с", "💧 70%", "⏱ 750 мм"),
            DayForecast("Вторник, 3 ноября", "+13° / +4°", "Осадки: Пасмурно ☁️", "💨 3.2 м/с", "💧 65%", "⏱ 753 мм"),
            DayForecast("Среда, 4 ноября", "+15° / +6°", "Осадки: Ясно ☀️", "💨 2.8 м/с", "💧 55%", "⏱ 756 мм"),
            DayForecast("Четверг, 5 ноября", "+10° / +2°", "Осадки: Дождь со снегом ❄️", "💨 6.0 м/с", "💧 82%", "⏱ 745 мм")
        )

        for (day in days) {
            val itemView = inflater.inflate(R.layout.item_weekly_weather, layoutWeeklyContainer, false)
            itemView.findViewById<TextView>(R.id.tvWeeklyDate).text = day.date
            itemView.findViewById<TextView>(R.id.tvWeeklyTemp).text = day.temp
            itemView.findViewById<TextView>(R.id.tvWeeklyPrecipitation).text = day.precipitation
            itemView.findViewById<TextView>(R.id.tvWeeklyWind).text = day.wind
            itemView.findViewById<TextView>(R.id.tvWeeklyHumidity).text = day.humidity
            itemView.findViewById<TextView>(R.id.tvWeeklyPressure).text = day.pressure
            layoutWeeklyContainer.addView(itemView)
        }
    }
}