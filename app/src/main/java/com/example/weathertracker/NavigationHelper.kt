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
        val weatherPair = getTabViews(activity, R.id.ivNavWeather, R.id.tvNavWeather) ?: return
        val forecastPair = getTabViews(activity, R.id.ivNavForecast, R.id.tvNavForecast) ?: return
        val reviewsPair = getTabViews(activity, R.id.ivNavReviews, R.id.tvNavReviews) ?: return
        val settingsPair = getTabViews(activity, R.id.ivNavSettings, R.id.tvNavSettings) ?: return

        resetTabs(weatherPair, forecastPair, reviewsPair, settingsPair)
        applyActiveTab(currentTab, weatherPair, forecastPair, reviewsPair, settingsPair)
        setupTabClickListeners(activity, currentTab)
    }

    private fun getTabViews(activity: Activity, iconId: Int, textId: Int): Pair<ImageView, TextView>? {
        val icon = activity.findViewById<ImageView>(iconId) ?: return null
        val text = activity.findViewById<TextView>(textId) ?: return null
        return Pair(icon, text)
    }

    private fun resetTabs(vararg tabs: Pair<ImageView, TextView>) {
        for (tab in tabs) {
            tab.first.setColorFilter(COLOR_INACTIVE)
            tab.second.setTextColor(COLOR_INACTIVE)
        }
    }

    private fun applyActiveTab(
        currentTab: Int,
        weather: Pair<ImageView, TextView>,
        forecast: Pair<ImageView, TextView>,
        reviews: Pair<ImageView, TextView>,
        settings: Pair<ImageView, TextView>
    ) {
        val activePair = when (currentTab) {
            TAB_WEATHER -> weather
            TAB_FORECAST -> forecast
            TAB_REVIEWS -> reviews
            TAB_SETTINGS -> settings
            else -> null
        }
        activePair?.first?.setColorFilter(COLOR_ACTIVE)
        activePair?.second?.setTextColor(COLOR_ACTIVE)
    }

    private fun setupTabClickListeners(activity: Activity, currentTab: Int) {
        bindTabClick(activity, R.id.navWeather, currentTab, TAB_WEATHER, MainActivity::class.java)
        bindTabClick(activity, R.id.navForecast, currentTab, TAB_FORECAST, WeatherDetailActivity::class.java)
        bindTabClick(activity, R.id.navReviews, currentTab, TAB_REVIEWS, ReviewsActivity::class.java)
        bindTabClick(activity, R.id.navSettings, currentTab, TAB_SETTINGS, SettingsActivity::class.java)
    }

    private fun bindTabClick(activity: Activity, buttonId: Int, currentTab: Int, targetTab: Int, targetClass: Class<*>) {
        val button = activity.findViewById<LinearLayout>(buttonId) ?: return
        button.setOnClickListener {
            if (currentTab != targetTab) {
                val intent = Intent(activity, targetClass).apply {
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                }
                activity.startActivity(intent)
            }
        }
    }
}