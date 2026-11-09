package com.example.weathertracker

object WeatherAppState {
    var currentCity: String = "Москва"
        set(value) {
            field = value
            AppPreferences.saveLastCity(value)
        }

    var currentLatitude: Double = 55.7558
    var currentLongitude: Double = 37.6173
}
