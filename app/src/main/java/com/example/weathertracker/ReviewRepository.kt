package com.example.weathertracker

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ReviewRepository {

    private lateinit var database: AppDatabase
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    fun init(context: Context) {
        database = AppDatabase.getDatabase(context)
        repositoryScope.launch {
            populateInitialReviewsIfEmpty()
        }
    }

    fun getReviewsFlow(): Flow<List<Review>> {
        return database.reviewDao().getAllReviews()
    }

    suspend fun getReviews(): List<Review> = withContext(Dispatchers.IO) {
        database.reviewDao().getAllReviewsList()
    }

    suspend fun addReview(review: Review) = withContext(Dispatchers.IO) {
        database.reviewDao().insertReview(review)
    }

    suspend fun removeReview(review: Review) = withContext(Dispatchers.IO) {
        database.reviewDao().deleteReview(review)
    }

    suspend fun updateReview(updatedReview: Review) = withContext(Dispatchers.IO) {
        database.reviewDao().updateReview(updatedReview)
    }

    private suspend fun populateInitialReviewsIfEmpty() {
        val existing = database.reviewDao().getAllReviewsList()
        if (existing.isEmpty()) {
            val initialList = createInitialReviews()
            database.reviewDao().insertAll(initialList)
        }
    }

    private fun createInitialReviews(): List<Review> {
        return createWesternReviews() + createEasternReviews()
    }

    private fun createWesternReviews(): List<Review> {
        return listOf(
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
            )
        )
    }

    private fun createEasternReviews(): List<Review> {
        return listOf(
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
    }
}
