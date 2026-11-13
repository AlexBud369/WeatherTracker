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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherMainFragment : Fragment() {

    companion object {
        fun newInstance(): WeatherMainFragment = WeatherMainFragment()
    }

    private val weatherViewModel: WeatherViewModel by viewModels { WeatherViewModelFactory() }

    private lateinit var etSearchCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var progressBarWeather: ProgressBar
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
        bindViews(view)
        setupResultListener()
        setupSearchListeners()
        setupNavigationButtons()
        observeViewModel()
        if (weatherViewModel.uiState.value.forecast == null) {
            weatherViewModel.loadWeather(WeatherAppState.currentCity)
        }
    }

    private fun bindViews(view: View) {
        etSearchCity = view.findViewById(R.id.etSearchCity)
        btnSearch = view.findViewById(R.id.btnSearch)
        progressBarWeather = view.findViewById(R.id.progressBarWeather)
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
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherViewModel.uiState.collect { state ->
                    renderUiState(state)
                }
            }
        }
    }

    private fun renderUiState(state: WeatherUiState) {
        setLoadingState(state.isLoading)
        state.errorMessage?.let { message ->
            Toast.makeText(requireContext(), "Ошибка: $message", Toast.LENGTH_LONG).show()
        }
        val city = state.city
        val forecast = state.forecast
        if (city != null && forecast != null) {
            displayWeather(city, forecast)
        }
    }

    private fun setupResultListener() {
        parentFragmentManager.setFragmentResultListener("review_added", viewLifecycleOwner) { _, bundle ->
            val title = bundle.getString("title") ?: "Отзыв о погоде"
            val city = bundle.getString("city") ?: WeatherAppState.currentCity
            val status = bundle.getString("status") ?: "Совпало"
            displayReviewNotice(title, city, status)
        }
    }

    private fun displayReviewNotice(title: String, city: String, status: String) {
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

    private fun setupSearchListeners() {
        btnSearch.setOnClickListener { performSearch() }
        etSearchCity.setOnEditorActionListener { _, actionId, event ->
            val isEnterKey = event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || isEnterKey) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupNavigationButtons() {
        btnShowDetails.setOnClickListener {
            val forecastFragment = ForecastFragment.newInstance(WeatherAppState.currentCity)
            (activity as? MainActivity)?.openFragment(forecastFragment, addToBackStack = true)
        }
        btnAddReview.setOnClickListener {
            val addReviewFragment = AddReviewFragment.newInstance(city = WeatherAppState.currentCity)
            (activity as? MainActivity)?.openFragment(addReviewFragment, addToBackStack = true)
        }
        cardMapPreview.setOnClickListener {
            val mapFragment = MapFragment.newInstance(WeatherAppState.currentCity)
            (activity as? MainActivity)?.openFragment(mapFragment, addToBackStack = true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvLatestReviewNotice.removeCallbacks(hideNoticeRunnable)
    }

    private fun performSearch() {
        val query = etSearchCity.text.toString().trim()
        if (query.isNotEmpty()) {
            WeatherAppState.currentCity = query
            hideKeyboard()
            weatherViewModel.loadWeather(query, force = true)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun displayWeather(city: GeocodingCity, forecast: ForecastResponse) {
        val current = forecast.current ?: return
        WeatherAppState.currentLatitude = city.latitude
        WeatherAppState.currentLongitude = city.longitude
        WeatherAppState.currentCity = city.name
        val countrySuffix = city.country?.let { ", $it" } ?: ""
        tvCityName.text = "${city.name}$countrySuffix"
        tvMapCityTitle.text = "Интерактивная карта осадков: ${city.name}"
        setWeatherMetrics(current)
        updateDateTime()
    }

    private fun setWeatherMetrics(current: CurrentWeatherDto) {
        tvCurrentTemp.text = WeatherMapper.formatTemperature(current.temperature)
        tvFeelsLike.text = WeatherMapper.formatFeelsLike(current.apparentTemperature)
        tvCondition.text = WeatherMapper.getWeatherDescription(current.weatherCode)
        tvWind.text = WeatherMapper.formatWindSpeed(current.windSpeed)
        tvHumidity.text = WeatherMapper.formatHumidity(current.relativeHumidity)
        tvPressure.text = WeatherMapper.formatPressure(current.surfacePressure)
    }

    private fun updateDateTime() {
        val formatter = SimpleDateFormat("d MMMM, HH:mm", Locale.forLanguageTag("ru"))
        tvDateTime.text = formatter.format(Date())
    }

    private fun setLoadingState(isLoading: Boolean) {
        progressBarWeather.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSearch.isEnabled = !isLoading
    }
}
