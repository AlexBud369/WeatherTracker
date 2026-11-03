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
import android.widget.TextView
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
    private lateinit var btnNetworkPosts: Button
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
        updateWeatherDisplay(WeatherAppState.currentCity)
    }

    private fun bindViews(view: View) {
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
        btnNetworkPosts = view.findViewById(R.id.btnNetworkPosts)
        tvLatestReviewNotice = view.findViewById(R.id.tvLatestReviewNotice)
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
        btnSearch.setOnClickListener {
            performSearch()
        }
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
        btnNetworkPosts.setOnClickListener {
            val postListFragment = PostListFragment.newInstance()
            (activity as? MainActivity)?.openFragment(postListFragment, addToBackStack = true)
        }
        cardMapPreview.setOnClickListener {
            val mapFragment = MapFragment.newInstance(WeatherAppState.currentCity)
            (activity as? MainActivity)?.openFragment(mapFragment, addToBackStack = true)
        }
    }

    override fun onResume() {
        super.onResume()
        updateWeatherDisplay(WeatherAppState.currentCity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvLatestReviewNotice.removeCallbacks(hideNoticeRunnable)
    }

    private fun performSearch() {
        val query = etSearchCity.text.toString().trim()
        if (query.isNotEmpty()) {
            WeatherAppState.currentCity = query
            updateWeatherDisplay(WeatherAppState.currentCity)
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
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
