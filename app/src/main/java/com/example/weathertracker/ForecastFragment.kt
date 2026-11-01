package com.example.weathertracker

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class ForecastFragment : Fragment() {

    companion object {
        private const val ARG_CITY = "arg_city"

        fun newInstance(city: String = WeatherAppState.currentCity): ForecastFragment {
            val fragment = ForecastFragment()
            val args = Bundle().apply {
                putString(ARG_CITY, city)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var etDetailCitySearch: EditText
    private lateinit var btnDetailSearch: Button
    private lateinit var layoutHourlyContainer: LinearLayout
    private lateinit var layoutWeeklyContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        readCityArgument()
        setupListeners()
        populateHourlyForecast()
        populateWeeklyForecast()
    }

    private fun bindViews(view: View) {
        etDetailCitySearch = view.findViewById(R.id.etDetailCitySearch)
        btnDetailSearch = view.findViewById(R.id.btnDetailSearch)
        layoutHourlyContainer = view.findViewById(R.id.layoutHourlyContainer)
        layoutWeeklyContainer = view.findViewById(R.id.layoutWeeklyContainer)
    }

    private fun readCityArgument() {
        val passedCity = arguments?.getString(ARG_CITY)?.takeIf { it.isNotBlank() }
        if (passedCity != null) {
            WeatherAppState.currentCity = passedCity
        }
        updateCityView(WeatherAppState.currentCity)
    }

    private fun setupListeners() {
        btnDetailSearch.setOnClickListener {
            performSearch()
        }
        etDetailCitySearch.setOnEditorActionListener { _, actionId, event ->
            val isEnterKey = event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || isEnterKey) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateCityView(WeatherAppState.currentCity)
    }

    private fun performSearch() {
        val searchedCity = etDetailCitySearch.text.toString().trim()
        if (searchedCity.isNotEmpty()) {
            WeatherAppState.currentCity = searchedCity
            updateCityView(WeatherAppState.currentCity)
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun updateCityView(cityName: String) {
        (activity as? MainActivity)?.updateForecastToolbar(cityName)
        etDetailCitySearch.setText(cityName)
    }

    private fun populateHourlyForecast() {
        layoutHourlyContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
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
        val inflater = LayoutInflater.from(requireContext())
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