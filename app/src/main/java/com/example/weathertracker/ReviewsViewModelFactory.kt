package com.example.weathertracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReviewsViewModelFactory(
    private val repository: ReviewRepository = ReviewRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewsViewModel::class.java)) {
            return ReviewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
