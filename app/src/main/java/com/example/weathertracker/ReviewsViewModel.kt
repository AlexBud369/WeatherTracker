package com.example.weathertracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewsViewModel(
    private val repository: ReviewRepository = ReviewRepository
) : ViewModel() {

    val reviews: StateFlow<List<Review>> = repository.getReviewsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            repository.removeReview(review)
        }
    }
}
