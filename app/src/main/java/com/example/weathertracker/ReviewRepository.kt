package com.example.weathertracker

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ReviewRepository {

    private lateinit var database: AppDatabase
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val reviewsChannel = MutableStateFlow<List<Review>>(emptyList())

    fun init(context: Context) {
        database = AppDatabase.getDatabase(context)
        repositoryScope.launch {
            populateInitialReviewsIfEmpty()
            refreshChannel()
        }
    }

    fun getReviewsFlow(): Flow<List<Review>> {
        return reviewsChannel
    }

    suspend fun getReviews(): List<Review> = withContext(Dispatchers.IO) {
        database.reviewDao().allReviews
    }

    suspend fun addReview(review: Review) = withContext(Dispatchers.IO) {
        database.reviewDao().insertReview(review)
        refreshChannel()
    }

    suspend fun removeReview(review: Review) = withContext(Dispatchers.IO) {
        database.reviewDao().deleteReview(review)
        refreshChannel()
    }

    suspend fun updateReview(updatedReview: Review) = withContext(Dispatchers.IO) {
        database.reviewDao().updateReview(updatedReview)
        refreshChannel()
    }

    private suspend fun refreshChannel() {
        val currentList = database.reviewDao().allReviews
        reviewsChannel.value = currentList
    }

    private suspend fun populateInitialReviewsIfEmpty() {
        val existing = database.reviewDao().allReviews
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
                0L,
                "Москва",
                "30.10.2026 10:45",
                "Совпало",
                "Прогноз был абсолютно точным: солнечно, тепло, температура совпала градус в градус.",
                "Точный прогноз на выходные"
            ),
            Review(
                0L,
                "Санкт-Петербург",
                "29.10.2026 15:30",
                "Не совпало",
                "Обещали ясную погоду без осадков, но после обеда пошел сильный дождь с ветром.",
                "Неожиданный дождь"
            )
        )
    }

    private fun createEasternReviews(): List<Review> {
        return listOf(
            Review(
                0L,
                "Казань",
                "28.10.2026 18:00",
                "Совпало",
                "Предупреждение о сильном ветре оправдалось. Было около 12 м/с.",
                "Ветрено в Казани"
            ),
            Review(
                0L,
                "Новосибирск",
                "27.10.2026 08:00",
                "Совпало",
                "Утром было прохладно, как и предсказывали в почасовом прогнозе.",
                "Утренний мороз"
            )
        )
    }
}
