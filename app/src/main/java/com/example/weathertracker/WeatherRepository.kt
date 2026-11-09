package com.example.weathertracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WeatherRepository {

    private val api = WeatherApiClient.service

    var latestCity: GeocodingCity? = null
        private set

    var latestForecast: ForecastResponse? = null
        private set

    suspend fun loadWeather(cityName: String): Result<Pair<GeocodingCity, ForecastResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val geocoding = api.searchCity(name = cityName)
                val city = geocoding.results?.firstOrNull()
                    ?: return@withContext Result.failure(Exception("Город «$cityName» не найден"))

                val forecast = api.getForecast(
                    latitude = city.latitude,
                    longitude = city.longitude
                )

                latestCity = city
                latestForecast = forecast
                Result.success(Pair(city, forecast))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
