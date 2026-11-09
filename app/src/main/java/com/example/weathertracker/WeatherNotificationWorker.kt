package com.example.weathertracker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WeatherNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        AppPreferences.init(applicationContext)
        val cityName = AppPreferences.getLastCity()
        val weatherResult = WeatherRepository.loadWeather(cityName)
        if (weatherResult.isSuccess) {
            val (city, forecast) = weatherResult.getOrThrow()
            sendSuccessNotification(city.name, forecast)
        } else {
            sendFallbackNotification(cityName)
        }
        return Result.success()
    }

    private fun sendSuccessNotification(city: String, forecast: ForecastResponse) {
        val current = forecast.current
        val temp = current?.temperature?.let { WeatherMapper.formatTemperature(it) } ?: "+18°C"
        val desc = current?.weatherCode?.let { WeatherMapper.getWeatherDescription(it) } ?: "Ясно ☀️"
        val title = "🌤️ Погода в городе $city"
        val message = "Сейчас $temp, $desc. Отличного вам дня!"
        NotificationHelper.showWeatherNotification(applicationContext, title, message)
    }

    private fun sendFallbackNotification(city: String) {
        val title = "🌤️ Погода: $city"
        val message = "Не забудьте проверить актуальный прогноз перед выходом на улицу!"
        NotificationHelper.showWeatherNotification(applicationContext, title, message)
    }
}
