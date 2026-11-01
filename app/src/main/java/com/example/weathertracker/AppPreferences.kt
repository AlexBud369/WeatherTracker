package com.example.weathertracker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AppPreferences {

    private const val PREFERENCES_NAME = "weather_tracker_prefs"
    private const val KEY_REVIEWS = "key_reviews_list"
    private const val KEY_LAST_CITY = "key_last_city"
    private const val KEY_DARK_THEME = "key_dark_theme"
    private const val KEY_NOTIFICATIONS = "key_notifications"
    private const val KEY_TEMPERATURE_UNIT = "key_temperature_unit"
    private const val KEY_LANGUAGE_INDEX = "key_language_index"

    private lateinit var preferences: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        preferences = context.applicationContext.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun saveReviews(reviews: List<Review>) {
        val jsonString = gson.toJson(reviews)
        preferences.edit().putString(KEY_REVIEWS, jsonString).apply()
    }

    fun getReviews(): List<Review> {
        val jsonString = preferences.getString(KEY_REVIEWS, null) ?: return emptyList()
        val reviewListType = object : TypeToken<List<Review>>() {}.type
        return runCatching {
            gson.fromJson<List<Review>>(jsonString, reviewListType)
        }.getOrNull() ?: emptyList()
    }

    fun saveLastCity(city: String) {
        preferences.edit().putString(KEY_LAST_CITY, city).apply()
    }

    fun getLastCity(): String {
        return preferences.getString(KEY_LAST_CITY, "Москва") ?: "Москва"
    }

    fun saveDarkTheme(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }

    fun isDarkTheme(): Boolean {
        return preferences.getBoolean(KEY_DARK_THEME, false)
    }

    fun saveNotifications(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun isNotificationsEnabled(): Boolean {
        return preferences.getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun saveTemperatureUnit(unit: String) {
        preferences.edit().putString(KEY_TEMPERATURE_UNIT, unit).apply()
    }

    fun getTemperatureUnit(): String {
        return preferences.getString(KEY_TEMPERATURE_UNIT, "°C") ?: "°C"
    }

    fun saveLanguageIndex(index: Int) {
        preferences.edit().putInt(KEY_LANGUAGE_INDEX, index).apply()
    }

    fun getLanguageIndex(): Int {
        return preferences.getInt(KEY_LANGUAGE_INDEX, 0)
    }
}
