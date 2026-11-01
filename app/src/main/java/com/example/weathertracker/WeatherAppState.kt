package com.example.weathertracker

object WeatherAppState {
    var currentCity: String = "Москва"
        set(value) {
            field = value
            AppPreferences.saveLastCity(value)
        }
}
