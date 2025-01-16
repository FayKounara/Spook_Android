package com.example.room_setup_composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.room_setup_composables.ui.theme.RoomDatabaseSetupTheme

//class MainActivity : ComponentActivity() {
//    private val bookingViewModel: BookingViewModel by viewModels()
//
//    private val storeViewModel: StoreViewModel by viewModels {
//        StoreViewModel.StoreViewModelFactory(AppDatabase.getDatabase(applicationContext).storeDao(), AppDatabase.getDatabase(applicationContext).offerDao())
//    }
//    private val reviewViewModel: ReviewViewModel by viewModels {
//        ReviewViewModel.ReviewViewModelFactory(AppDatabase.getDatabase(applicationContext).reviewDao())
//    }
//
//    private val userViewModel: UserViewModel by viewModels {
//        UserViewModel.UserViewModelFactory(AppDatabase.getDatabase(applicationContext).userDao())
//    }
//    private val slotViewModel: SlotViewModel by viewModels {
//        SlotViewModel.SlotViewModelFactory(
//            AppDatabase.getDatabase(applicationContext).slotDao(),AppDatabase.getDatabase(applicationContext).storeDao())
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        //storeViewModel.resetAndInsertMockStores()
//        setContent {
//            RoomDatabaseSetupTheme {
//                LoginNavigation(userViewModel, storeViewModel, bookingViewModel, reviewViewModel,slotViewModel)
//            }
//        }
//    }
//}



import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.room_setup_composables.ui.theme.RoomDatabaseSetupTheme
import kotlinx.coroutines.delay
import com.example.room_database_setup.R

import androidx.compose.animation.core.*



class MainActivity : ComponentActivity() {
    private val bookingViewModel: BookingViewModel by viewModels()

    private val storeViewModel: StoreViewModel by viewModels {
        StoreViewModel.StoreViewModelFactory(
            AppDatabase.getDatabase(applicationContext).storeDao(),
            AppDatabase.getDatabase(applicationContext).offerDao()
        )
    }
    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModel.ReviewViewModelFactory(AppDatabase.getDatabase(applicationContext).reviewDao())
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(AppDatabase.getDatabase(applicationContext).userDao())
    }
    private val slotViewModel: SlotViewModel by viewModels {
        SlotViewModel.SlotViewModelFactory(
            AppDatabase.getDatabase(applicationContext).slotDao(),
            AppDatabase.getDatabase(applicationContext).storeDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoomDatabaseSetupTheme {
                MainScreen(
                    userViewModel = userViewModel,
                    storeViewModel = storeViewModel,
                    bookingViewModel = bookingViewModel,
                    reviewViewModel = reviewViewModel,
                    slotViewModel = slotViewModel
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    storeViewModel: StoreViewModel,
    bookingViewModel: BookingViewModel,
    reviewViewModel: ReviewViewModel,
    slotViewModel: SlotViewModel
) {
    var showSplashScreen by remember { mutableStateOf(true) }

    if (showSplashScreen) {
        SplashScreen {
            showSplashScreen = false
        }
    } else {
        LoginNavigation(
            userViewModel = userViewModel,
            storeViewModel = storeViewModel,
            bookingViewModel = bookingViewModel,
            reviewViewModel = reviewViewModel,
            slotViewModel = slotViewModel
        )
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.juicylogo2),
            contentDescription = "App Logo",
            modifier = Modifier.size(400.dp).graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
        )
    }
}

