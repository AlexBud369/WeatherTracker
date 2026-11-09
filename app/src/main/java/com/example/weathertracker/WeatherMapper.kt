package com.example.weathertracker

import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

object WeatherMapper {

    fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Ясно ☀️"
            1, 2 -> "Переменная облачность ⛅"
            3 -> "Пасмурно ☁️"
            45, 48 -> "Туман 🌫️"
            51, 53, 55 -> "Морось 🌦️"
            61, 63, 65 -> "Дождь 🌧️"
            71, 73, 75, 77 -> "Снегопад ❄️"
            80, 81, 82 -> "Ливень 🌧️"
            85, 86 -> "Метель 🌨️"
            95, 96, 99 -> "Гроза ⛈️"
            else -> "Облачно ⛅"
        }
    }

    fun getWeatherEmoji(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1, 2 -> "⛅"
            3 -> "☁️"
            45, 48 -> "🌫️"
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> "🌧️"
            71, 73, 75, 77, 85, 86 -> "❄️"
            95, 96, 99 -> "⛈️"
            else -> "⛅"
        }
    }

    fun formatTemperature(temp: Double): String {
        val rounded = temp.roundToInt()
        return if (rounded > 0) "+$rounded°C" else "$rounded°C"
    }

    fun formatFeelsLike(temp: Double): String {
        val rounded = temp.roundToInt()
        val formatted = if (rounded > 0) "+$rounded°C" else "$rounded°C"
        return "Ощущается как $formatted"
    }

    fun formatWindSpeed(kmh: Double): String {
        val ms = (kmh / 3.6 * 10).roundToInt() / 10.0
        return "$ms м/с"
    }

    fun formatPressure(hPa: Double): String {
        val mm = (hPa * 0.750062).roundToInt()
        return "$mm мм"
    }

    fun formatHumidity(humidity: Int): String {
        return "$humidity%"
    }

    fun formatHour(isoTime: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = parser.parse(isoTime)
            if (date != null) formatter.format(date) else isoTime
        } catch (e: Exception) {
            isoTime
        }
    }

    fun formatDay(isoDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag("ru"))
            val date = parser.parse(isoDate)
            if (date != null) {
                val formatted = formatter.format(date)
                formatted.replaceFirstChar { it.uppercase() }
            } else {
                isoDate
            }
        } catch (e: Exception) {
            isoDate
        }
    }
}
