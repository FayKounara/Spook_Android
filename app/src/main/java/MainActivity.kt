package com.example.room_setup_composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.room_setup_composables.ui.theme.RoomDatabaseSetupTheme
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class MainActivity : ComponentActivity() {
    private val bookingViewModel: BookingViewModel by viewModels()

    private val storeViewModel: StoreViewModel by viewModels {
        StoreViewModel.StoreViewModelFactory(AppDatabase.getDatabase(applicationContext).storeDao(), AppDatabase.getDatabase(applicationContext).offerDao())
    }
    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModel.ReviewViewModelFactory(AppDatabase.getDatabase(applicationContext).reviewDao())
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(AppDatabase.getDatabase(applicationContext).userDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //storeViewModel.resetAndInsertMockStores()

        setContent {

            RoomDatabaseSetupTheme {
                AuthNavigation(userViewModel, storeViewModel, bookingViewModel, reviewViewModel)
                //HomePageNavigation(userId = 1, storeViewModel, bookingViewModel, reviewViewModel)
            }
        }
    }
}


