package com.example.room_setup_composables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userDao: UserDao) : ViewModel() {

    //all users as flow
    val allUsers: Flow<List<User>> = userDao.getAllUsers()


    fun insertUser(user: User) {
        viewModelScope.launch {
            userDao.insert(user)
        }
    }

    private val _userDetails = MutableStateFlow("")
    private val _email   = MutableStateFlow("")
    val userDetails: StateFlow<String> = _userDetails
    val email: StateFlow<String> = _email
    //get user's username
    fun fetchUserName(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) { // Run the database operation in the background
            val username = userDao.getName(userId)


            withContext(Dispatchers.Main) {
                _userDetails.value = username ?: "Unknown"
            }
        }
    }
    //get email
    fun fetchEmail(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val email = userDao.getEmail(userId)

            withContext(Dispatchers.Main) {
                _email.value = email ?: "Unknown"
            }
        }
    }


        class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UserViewModel(userDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

    }

