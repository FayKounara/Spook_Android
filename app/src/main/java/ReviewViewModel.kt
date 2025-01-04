package com.example.room_setup_composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewDao: ReviewDao) : ViewModel() {

    //all reviews as flow
    val allReviews: Flow<List<Review>> = reviewDao.getReviews()

//    init {
//        deleteAllReviews()
//    }
    fun insertReview(review: Review) {
        viewModelScope.launch {
            reviewDao.insert(review)
        }
    }

    fun deleteAllReviews() {
        viewModelScope.launch {
            reviewDao.deleteAllReviews()
        }
    }

    class ReviewViewModelFactory(private val reviewDao: ReviewDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(reviewDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
