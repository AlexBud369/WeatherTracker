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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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
    private lateinit var progressBarForecast: ProgressBar
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
        loadForecast(WeatherAppState.currentCity)
    }

    private fun bindViews(view: View) {
        etDetailCitySearch = view.findViewById(R.id.etDetailCitySearch)
        btnDetailSearch = view.findViewById(R.id.btnDetailSearch)
        progressBarForecast = view.findViewById(R.id.progressBarForecast)
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
        btnDetailSearch.setOnClickListener { performSearch() }
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

    private fun performSearch() {
        val searchedCity = etDetailCitySearch.text.toString().trim()
        if (searchedCity.isNotEmpty()) {
            WeatherAppState.currentCity = searchedCity
            updateCityView(WeatherAppState.currentCity)
            hideKeyboard()
            loadForecast(searchedCity)
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

    private fun loadForecast(cityName: String) {
        val cached = WeatherRepository.latestForecast
        val cachedCity = WeatherRepository.latestCity
        if (cached != null && cachedCity?.name?.equals(cityName, ignoreCase = true) == true) {
            renderForecast(cached)
            return
        }
        fetchForecastData(cityName)
    }

    private fun fetchForecastData(cityName: String) {
        setLoadingState(true)
        viewLifecycleOwner.lifecycleScope.launch {
            val result = WeatherRepository.loadWeather(cityName)
            setLoadingState(false)
            result.onSuccess { pair ->
                updateCityView(pair.first.name)
                renderForecast(pair.second)
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Ошибка: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        progressBarForecast.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnDetailSearch.isEnabled = !isLoading
    }

    private fun renderForecast(forecast: ForecastResponse) {
        populateHourlyForecast(forecast.hourly)
        populateWeeklyForecast(forecast.daily)
    }

    private fun populateHourlyForecast(hourly: HourlyWeatherDto?) {
        layoutHourlyContainer.removeAllViews()
        val times = hourly?.time ?: return
        val temps = hourly.temperature ?: return
        val codes = hourly.weatherCode ?: return
        val count = minOf(24, times.size, temps.size, codes.size)
        val inflater = LayoutInflater.from(requireContext())
        for (i in 0 until count) {
            val itemView = inflater.inflate(R.layout.item_hourly_weather, layoutHourlyContainer, false)
            itemView.findViewById<TextView>(R.id.tvHourlyTime).text = WeatherMapper.formatHour(times[i])
            itemView.findViewById<TextView>(R.id.tvHourlyIcon).text = WeatherMapper.getWeatherEmoji(codes[i])
            itemView.findViewById<TextView>(R.id.tvHourlyTemp).text = WeatherMapper.formatTemperature(temps[i])
            layoutHourlyContainer.addView(itemView)
        }
    }

    private fun populateWeeklyForecast(daily: DailyWeatherDto?) {
        layoutWeeklyContainer.removeAllViews()
        val dates = daily?.time ?: return
        val maxTemps = daily.maxTemp ?: return
        val minTemps = daily.minTemp ?: return
        val codes = daily.weatherCode ?: return
        val count = minOf(dates.size, maxTemps.size, minTemps.size, codes.size)
        val inflater = LayoutInflater.from(requireContext())
        for (i in 0 until count) {
            val itemView = inflater.inflate(R.layout.item_weekly_weather, layoutWeeklyContainer, false)
            bindWeeklyItem(itemView, dates[i], maxTemps[i], minTemps[i], codes[i])
            layoutWeeklyContainer.addView(itemView)
        }
    }

    private fun bindWeeklyItem(view: View, date: String, max: Double, min: Double, code: Int) {
        view.findViewById<TextView>(R.id.tvWeeklyDate).text = WeatherMapper.formatDay(date)
        view.findViewById<TextView>(R.id.tvWeeklyTemp).text =
            "${WeatherMapper.formatTemperature(max)} / ${WeatherMapper.formatTemperature(min)}"
        view.findViewById<TextView>(R.id.tvWeeklyPrecipitation).text =
            "Осадки: ${WeatherMapper.getWeatherDescription(code)}"
    }
}
