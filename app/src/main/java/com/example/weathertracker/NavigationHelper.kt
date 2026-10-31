package com.example.weathertracker

import android.app.Activity
import android.graphics.Color
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

object NavigationHelper {

    const val TAB_WEATHER = 1
    const val TAB_FORECAST = 2
    const val TAB_REVIEWS = 3
    const val TAB_SETTINGS = 4
    const val TAB_NONE = 0

    private val COLOR_ACTIVE = Color.parseColor("#1E88E5")
    private val COLOR_INACTIVE = Color.parseColor("#718096")

    fun highlightTab(activity: Activity, currentTab: Int) {
        val ivNavWeather = activity.findViewById<ImageView>(R.id.ivNavWeather) ?: return
        val tvNavWeather = activity.findViewById<TextView>(R.id.tvNavWeather) ?: return
        val ivNavForecast = activity.findViewById<ImageView>(R.id.ivNavForecast) ?: return
        val tvNavForecast = activity.findViewById<TextView>(R.id.tvNavForecast) ?: return
        val ivNavReviews = activity.findViewById<ImageView>(R.id.ivNavReviews) ?: return
        val tvNavReviews = activity.findViewById<TextView>(R.id.tvNavReviews) ?: return
        val ivNavSettings = activity.findViewById<ImageView>(R.id.ivNavSettings) ?: return
        val tvNavSettings = activity.findViewById<TextView>(R.id.tvNavSettings) ?: return

        // Сброс всех вкладок в неактивное состояние
        ivNavWeather.setColorFilter(COLOR_INACTIVE)
        tvNavWeather.setTextColor(COLOR_INACTIVE)
        ivNavForecast.setColorFilter(COLOR_INACTIVE)
        tvNavForecast.setTextColor(COLOR_INACTIVE)
        ivNavReviews.setColorFilter(COLOR_INACTIVE)
        tvNavReviews.setTextColor(COLOR_INACTIVE)
        ivNavSettings.setColorFilter(COLOR_INACTIVE)
        tvNavSettings.setTextColor(COLOR_INACTIVE)

        // Подсветка активной вкладки
        when (currentTab) {
            TAB_WEATHER -> {
                ivNavWeather.setColorFilter(COLOR_ACTIVE)
                tvNavWeather.setTextColor(COLOR_ACTIVE)
            }
            TAB_FORECAST -> {
                ivNavForecast.setColorFilter(COLOR_ACTIVE)
                tvNavForecast.setTextColor(COLOR_ACTIVE)
            }
            TAB_REVIEWS -> {
                ivNavReviews.setColorFilter(COLOR_ACTIVE)
                tvNavReviews.setTextColor(COLOR_ACTIVE)
            }
            TAB_SETTINGS -> {
                ivNavSettings.setColorFilter(COLOR_ACTIVE)
                tvNavSettings.setTextColor(COLOR_ACTIVE)
            }
        }
    }

    fun setupBottomNav(activity: Activity, onTabSelected: (Int) -> Unit) {
        val navWeather = activity.findViewById<LinearLayout>(R.id.navWeather) ?: return
        val navForecast = activity.findViewById<LinearLayout>(R.id.navForecast) ?: return
        val navReviews = activity.findViewById<LinearLayout>(R.id.navReviews) ?: return
        val navSettings = activity.findViewById<LinearLayout>(R.id.navSettings) ?: return

        navWeather.setOnClickListener { onTabSelected(TAB_WEATHER) }
        navForecast.setOnClickListener { onTabSelected(TAB_FORECAST) }
        navReviews.setOnClickListener { onTabSelected(TAB_REVIEWS) }
        navSettings.setOnClickListener { onTabSelected(TAB_SETTINGS) }
    }
}