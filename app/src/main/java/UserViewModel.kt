package com.example.room_setup_composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    //all users as flow
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    fun insertUser(user: User) {
        viewModelScope.launch {
            userDao.insert(user)
        }
    }

    // Factory for creating StoreViewModel with StoreDao
    class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
