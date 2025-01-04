package com.example.room_setup_composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.room_setup_composables.ui.theme.RoomDatabaseSetupTheme

class MainActivity : ComponentActivity() {
    private val bookingViewModel: BookingViewModel by viewModels()
    private val storeViewModel: StoreViewModel by viewModels {
        StoreViewModel.StoreViewModelFactory(AppDatabase.getDatabase(applicationContext).storeDao(), AppDatabase.getDatabase(applicationContext).offerDao())
    }
    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModel.ReviewViewModelFactory(AppDatabase.getDatabase(applicationContext).reviewDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //storeViewModel.resetAndInsertMockStores()

        setContent {
            RoomDatabaseSetupTheme {
                //AuthNavigation(storeViewModel, bookingViewModel, reviewViewModel);
                HomePageNavigation(storeViewModel, bookingViewModel, reviewViewModel);
            }
        }
    }
}


