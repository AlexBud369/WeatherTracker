package com.example.weathertracker

object ReviewRepository {
    private val reviews = mutableListOf(
        Review(
            id = 1L,
            city = "Москва",
            date = "30.10.2026 10:45",
            status = "Совпало",
            comment = "Прогноз был абсолютно точным: солнечно, тепло, температура совпала градус в градус.",
            title = "Точный прогноз на выходные"
        ),
        Review(
            id = 2L,
            city = "Санкт-Петербург",
            date = "29.10.2026 15:30",
            status = "Не совпало",
            comment = "Обещали ясную погоду без осадков, но после обеда пошел сильный дождь с ветром.",
            title = "Неожиданный дождь"
        ),
        Review(
            id = 3L,
            city = "Казань",
            date = "28.10.2026 18:00",
            status = "Совпало",
            comment = "Предупреждение о сильном ветре оправдалось. Было около 12 м/с.",
            title = "Ветрено в Казани"
        ),
        Review(
            id = 4L,
            city = "Новосибирск",
            date = "27.10.2026 08:00",
            status = "Совпало",
            comment = "Утром было прохладно, как и предсказывали в почасовом прогнозе.",
            title = "Утренний мороз"
        )
    )

    fun getReviews(): MutableList<Review> = reviews.toMutableList()

    fun addReview(review: Review) {
        reviews.add(0, review)
    }

    fun removeReview(review: Review) {
        val index = reviews.indexOfFirst { it.id == review.id }
        if (index != -1) {
            reviews.removeAt(index)
        }
    }

    fun updateReview(updatedReview: Review) {
        val index = reviews.indexOfFirst { it.id == updatedReview.id }
        if (index != -1) {
            reviews[index] = updatedReview
        }
    }
}