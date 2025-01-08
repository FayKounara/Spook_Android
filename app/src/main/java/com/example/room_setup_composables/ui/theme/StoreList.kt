package com.example.room_setup_composables.com.example.room_setup_composables.ui.theme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_database_setup.R
import com.example.room_setup_composables.BookingViewModel
import com.example.room_setup_composables.BookingsScreen
import com.example.room_setup_composables.Store
import com.example.room_setup_composables.StoreViewModel
import com.example.room_setup_composables.UserViewModel
import com.example.room_setup_composables.ui.theme.Screen

@Composable
fun StoreNavigation(userId: Int, userViewModel: UserViewModel, storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, filtername: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Stores.route) {
        composable(route = Screen.Stores.route) {
            StoreList(navController, storeViewModel, filtername)
        }
        composable(
            route = Screen.Bookings.route + "/{hour}/{storeId}",
            arguments = listOf(
                navArgument("hour") {
                    type = NavType.StringType
                    defaultValue = "0"
                },
                navArgument("storeId") {
                    type = NavType.StringType
                    defaultValue = "1"
                }
            )
        ) { entry ->
            val hour = entry.arguments?.getString("hour") ?: "0"
            val storeId = entry.arguments?.getString("storeId") ?: "1"
            BookingsScreen(userId, userViewModel, navController, bookingViewModel, hour, storeId)
        }
    }
}

@Composable
fun StoreList(navController: NavController, viewModel: StoreViewModel, filtername: String) {
    val stores by viewModel.allStores.collectAsState(initial = emptyList())
    val filteredStores = stores.filter { it.name == filtername }

    if (filteredStores.isNotEmpty()) {
        val store = filteredStores.first()
        val allAvailableHours = filteredStores.flatMap { it.avHours.split(",") }.distinct()
        StoreCard(navController, store, allAvailableHours)
    }
}

@Composable
fun StoreCard(navController: NavController, store: Store, availableHours: List<String>) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFF1F3F4)), // Background για όλη την κάρτα
        colors = CardDefaults.cardColors(containerColor = Color.White) // Η κάρτα να είναι λευκή
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Προσθήκη Εικόνας
            Image(
                painter = painterResource(id = R.drawable.restphoto), // Replace with actual image
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Πληροφορίες Καταστήματος
            Text(
                text = store.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StoreDetailsSection(store)

            Spacer(modifier = Modifier.height(16.dp))

            // Διαθέσιμες Ώρες
            Text(
                text = "Available Hours:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableHours.forEach { hour ->
                    HourButton(hour = hour.trim()) {
                        Log.d("NavigationDebug", "Navigating with hour=$hour and storeId=${store.storeId}")
                        navController.navigate(Screen.Bookings.withArgs(hour.trim(), store.storeId.toString()))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Κουμπί Κράτησης
            BookNowButton(navController)
        }
    }
}

@Composable
fun StoreDetailsSection(store: Store) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = store.location,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "10:30 AM - 11:00 PM", // Replace with actual opening hours
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun HourButton(hour: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)) // Χρώμα πορτοκαλί για τα κουμπιά
    ) {
        Text(text = hour)
    }
}

@Composable
fun BookNowButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Screen.Bookings.withArgs()) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)) // Πορτοκαλί χρώμα
    ) {
        Text(
            text = "Proceed with reservation :)",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
