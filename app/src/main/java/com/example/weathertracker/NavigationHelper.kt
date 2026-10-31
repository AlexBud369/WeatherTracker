package com.example.weathertracker

import android.app.Activity
import android.content.Intent
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

    fun setupBottomNav(activity: Activity, currentTab: Int) {
        val navWeather = activity.findViewById<LinearLayout>(R.id.navWeather) ?: return
        val navForecast = activity.findViewById<LinearLayout>(R.id.navForecast) ?: return
        val navReviews = activity.findViewById<LinearLayout>(R.id.navReviews) ?: return
        val navSettings = activity.findViewById<LinearLayout>(R.id.navSettings) ?: return

        val ivNavWeather = activity.findViewById<ImageView>(R.id.ivNavWeather)
        val tvNavWeather = activity.findViewById<TextView>(R.id.tvNavWeather)
        val ivNavForecast = activity.findViewById<ImageView>(R.id.ivNavForecast)
        val tvNavForecast = activity.findViewById<TextView>(R.id.tvNavForecast)
        val ivNavReviews = activity.findViewById<ImageView>(R.id.ivNavReviews)
        val tvNavReviews = activity.findViewById<TextView>(R.id.tvNavReviews)
        val ivNavSettings = activity.findViewById<ImageView>(R.id.ivNavSettings)
        val tvNavSettings = activity.findViewById<TextView>(R.id.tvNavSettings)

        // Reset all to inactive
        ivNavWeather?.setColorFilter(COLOR_INACTIVE)
        tvNavWeather?.setTextColor(COLOR_INACTIVE)
        ivNavForecast?.setColorFilter(COLOR_INACTIVE)
        tvNavForecast?.setTextColor(COLOR_INACTIVE)
        ivNavReviews?.setColorFilter(COLOR_INACTIVE)
        tvNavReviews?.setTextColor(COLOR_INACTIVE)
        ivNavSettings?.setColorFilter(COLOR_INACTIVE)
        tvNavSettings?.setTextColor(COLOR_INACTIVE)

        // Set active tab
        when (currentTab) {
            TAB_WEATHER -> {
                ivNavWeather?.setColorFilter(COLOR_ACTIVE)
                tvNavWeather?.setTextColor(COLOR_ACTIVE)
            }
            TAB_FORECAST -> {
                ivNavForecast?.setColorFilter(COLOR_ACTIVE)
                tvNavForecast?.setTextColor(COLOR_ACTIVE)
            }
            TAB_REVIEWS -> {
                ivNavReviews?.setColorFilter(COLOR_ACTIVE)
                tvNavReviews?.setTextColor(COLOR_ACTIVE)
            }
            TAB_SETTINGS -> {
                ivNavSettings?.setColorFilter(COLOR_ACTIVE)
                tvNavSettings?.setTextColor(COLOR_ACTIVE)
            }
        }

        navWeather.setOnClickListener {
            if (currentTab != TAB_WEATHER) {
                val intent = Intent(activity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                activity.startActivity(intent)
            }
        }

        navForecast.setOnClickListener {
            if (currentTab != TAB_FORECAST) {
                val intent = Intent(activity, WeatherDetailActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                activity.startActivity(intent)
            }
        }

        navReviews.setOnClickListener {
            if (currentTab != TAB_REVIEWS) {
                val intent = Intent(activity, ReviewsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                activity.startActivity(intent)
            }
        }

        navSettings.setOnClickListener {
            if (currentTab != TAB_SETTINGS) {
                val intent = Intent(activity, SettingsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                activity.startActivity(intent)
            }
        }
    }
}
