package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class WeatherMainFragment : Fragment() {

    companion object {
        fun newInstance(): WeatherMainFragment = WeatherMainFragment()
    }

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
        view?.let {
            tvLatestReviewNotice.animate()
                .alpha(0f)
                .setDuration(400)
                .withEndAction {
                    tvLatestReviewNotice.visibility = View.GONE
                    tvLatestReviewNotice.alpha = 1f
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearchCity = view.findViewById(R.id.etSearchCity)
        btnSearch = view.findViewById(R.id.btnSearch)
        tvCityName = view.findViewById(R.id.tvCityName)
        tvCondition = view.findViewById(R.id.tvCondition)
        tvDateTime = view.findViewById(R.id.tvDateTime)
        tvCurrentTemp = view.findViewById(R.id.tvCurrentTemp)
        tvFeelsLike = view.findViewById(R.id.tvFeelsLike)
        cardMapPreview = view.findViewById(R.id.cardMapPreview)
        tvMapCityTitle = view.findViewById(R.id.tvMapCityTitle)
        tvWind = view.findViewById(R.id.tvWind)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        tvPressure = view.findViewById(R.id.tvPressure)
        btnShowDetails = view.findViewById(R.id.btnShowDetails)
        btnAddReview = view.findViewById(R.id.btnAddReview)
        tvLatestReviewNotice = view.findViewById(R.id.tvLatestReviewNotice)

        updateWeatherDisplay(currentCity)

        // Слушатель результатов из дочерних фрагментов (Fragment Result API, Шаг 5 Лаб 9)
        parentFragmentManager.setFragmentResultListener(
            "review_added",
            viewLifecycleOwner
        ) { _, bundle ->
            val title = bundle.getString("title") ?: "Отзыв о погоде"
            val city = bundle.getString("city") ?: currentCity
            val status = bundle.getString("status") ?: "Совпало"

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

        // Поиск города
        btnSearch.setOnClickListener {
            val query = etSearchCity.text.toString().trim()
            if (query.isNotEmpty()) {
                currentCity = query
                updateWeatherDisplay(currentCity)
                Toast.makeText(requireContext(), "Город изменен на: $currentCity", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Введите название города для поиска", Toast.LENGTH_SHORT).show()
            }
        }

        // Переход на экран подробного прогноза (Экран 2) с передачей города через Bundle
        btnShowDetails.setOnClickListener {
            val forecastFragment = ForecastFragment.newInstance(currentCity)
            (activity as? MainActivity)?.openFragment(forecastFragment, addToBackStack = true)
        }

        // Открытие экрана добавления отзыва (Экран 4) с передачей города
        btnAddReview.setOnClickListener {
            val addReviewFragment = AddReviewFragment.newInstance(city = currentCity)
            (activity as? MainActivity)?.openFragment(addReviewFragment, addToBackStack = true)
        }

        // Открытие интерактивной карты (экран карты поверх экрана)
        cardMapPreview.setOnClickListener {
            val mapFragment = MapFragment.newInstance(currentCity)
            (activity as? MainActivity)?.openFragment(mapFragment, addToBackStack = true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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