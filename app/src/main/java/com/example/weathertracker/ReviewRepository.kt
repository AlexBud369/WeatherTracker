package com.example.weathertracker

object ReviewRepository {
    private val reviews = mutableListOf(
        Review(
            city = "Москва",
            date = "30.10.2026 10:45",
            status = "Совпало",
            comment = "Прогноз был абсолютно точным: солнечно, тепло, температура совпала градус в градус.",
            title = "Точный прогноз на выходные"
        ),
        Review(
            city = "Санкт-Петербург",
            date = "29.10.2026 15:30",
            status = "Не совпало",
            comment = "Обещали ясную погоду без осадков, но после обеда пошел сильный дождь с ветром.",
            title = "Неожиданный дождь"
        ),
        Review(
            city = "Казань",
            date = "28.10.2026 18:00",
            status = "Совпало",
            comment = "Предупреждение о сильном ветре оправдалось. Было около 12 м/с.",
            title = "Ветрено в Казани"
        ),
        Review(
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
        reviews.removeAll { it.id == review.id || (it.city == review.city && it.date == review.date && it.comment == review.comment) }
    }

    fun updateReview(updatedReview: Review) {
        val index = reviews.indexOfFirst { it.id == updatedReview.id }
        if (index != -1) {
            reviews[index] = updatedReview
        }
    }
}
