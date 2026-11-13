package com.example.weathertracker

data class WeatherUiState(
    val isLoading: Boolean = false,
    val city: GeocodingCity? = null,
    val forecast: ForecastResponse? = null,
    val errorMessage: String? = null
)
