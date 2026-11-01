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
        val weatherPair = getTabViews(activity, R.id.ivNavWeather, R.id.tvNavWeather) ?: return
        val forecastPair = getTabViews(activity, R.id.ivNavForecast, R.id.tvNavForecast) ?: return
        val reviewsPair = getTabViews(activity, R.id.ivNavReviews, R.id.tvNavReviews) ?: return
        val settingsPair = getTabViews(activity, R.id.ivNavSettings, R.id.tvNavSettings) ?: return

        resetTabs(weatherPair, forecastPair, reviewsPair, settingsPair)
        applyActiveTab(currentTab, weatherPair, forecastPair, reviewsPair, settingsPair)
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