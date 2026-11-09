package com.example.weathertracker

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    @SerializedName("results")
    val results: List<GeocodingCity>?
)

data class GeocodingCity(
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("country")
    val country: String? = null
)

data class ForecastResponse(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timezone")
    val timezone: String?,
    @SerializedName("current")
    val current: CurrentWeatherDto?,
    @SerializedName("hourly")
    val hourly: HourlyWeatherDto?,
    @SerializedName("daily")
    val daily: DailyWeatherDto?
)

data class CurrentWeatherDto(
    @SerializedName("time")
    val time: String,
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("apparent_temperature")
    val apparentTemperature: Double,
    @SerializedName("relative_humidity_2m")
    val relativeHumidity: Int,
    @SerializedName("weather_code")
    val weatherCode: Int,
    @SerializedName("surface_pressure")
    val surfacePressure: Double,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double
)

data class HourlyWeatherDto(
    @SerializedName("time")
    val time: List<String>?,
    @SerializedName("temperature_2m")
    val temperature: List<Double>?,
    @SerializedName("weather_code")
    val weatherCode: List<Int>?
)

data class DailyWeatherDto(
    @SerializedName("time")
    val time: List<String>?,
    @SerializedName("weather_code")
    val weatherCode: List<Int>?,
    @SerializedName("temperature_2m_max")
    val maxTemp: List<Double>?,
    @SerializedName("temperature_2m_min")
    val minTemp: List<Double>?,
    @SerializedName("wind_speed_10m_max")
    val maxWindSpeed: List<Double>?,
    @SerializedName("precipitation_probability_max")
    val precipitationProbability: List<Int>?
)
