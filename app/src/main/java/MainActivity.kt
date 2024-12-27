package com.example.room_setup_composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.room_setup_composables.ui.theme.RoomDatabaseSetupTheme

class MainActivity : ComponentActivity() {
    private val viewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomDatabaseSetupTheme {
                HomePageNavigation(viewModel);
            }
        }
    }
}


