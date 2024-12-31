package com.example.room_setup_composables.com.example.room_setup_composables.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_setup_composables.BookingNavigation
import com.example.room_setup_composables.BookingViewModel
import com.example.room_setup_composables.Store
import com.example.room_setup_composables.StoreViewModel
import com.example.room_setup_composables.ui.theme.Screen

@Composable
fun StoreNavigation(storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, filtername: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Stores.route) {
        composable(route = Screen.Stores.route) {
            StoreList(navController, storeViewModel, filtername);
        }
        composable(
            route = Screen.Bookings.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "John"
                    nullable = true
                }
            )
        ) { entry ->
            val name = entry.arguments?.getString("name") ?: "John"
            BookingNavigation(bookingViewModel, name)
        }
    }
}


@Composable
fun  StoreList(navController:NavController ,viewModel: StoreViewModel, filtername: String) {
    // Collect the list of stores as state
    val stores by viewModel.allStores.collectAsState(initial = emptyList())

    // Filter the stores to get the store with the given name (filtername)
    val filteredStores = stores.filter { it.name == filtername }

    ToBookPage(navController)
    // If there are stores with the same name, group by the name and combine the time slots
    if (filteredStores.isNotEmpty()) {
        // Assuming we want to show only one card per store (grouped by name)
        val store = filteredStores.first() // Get the first store with the given name
        val allAvailableHours = filteredStores.flatMap { it.avHours.split(",") }.distinct()

        // Display one card for the store and all its available time slots
        StoreCard(navController, store, allAvailableHours)
    }
}

@Composable
fun StoreCard(navController:NavController, store: Store, availableHours: List<String>) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp), // Rounded corners for the card
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Restaurant Name and Image (displayed only once)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Restaurant Name
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

//                // Restaurant Image (Placeholder image for now)
//                Image(
//                    painter = painterResource(id = R.drawable.restaurant_image), // Replace with actual image resource
//                    contentDescription = "Restaurant Image",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp) // Adjust height as needed
//                        .padding(bottom = 8.dp)
//                )
            }

            // Restaurant Information (e.g., Info, Location)
            Text(
                text = "Info: ${store.info}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Location: ${store.location}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Available Hours Header (Only once for the restaurant)
            Text(
                text = "Available Hours:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Display all available hours (separate clickable boxes for each hour)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Space out the boxes
            ) {
                availableHours.forEach { hour ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(40.dp) // Control height of each hour box
                            .weight(1f) // This ensures the boxes stretch equally across the available space
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                // Handle the click event for each hour here
                                println("Clicked on hour: $hour")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hour.trim(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToBookPage(navController: NavController) {
    var text by remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            navController.navigate(Screen.Bookings.withArgs(text))
        }) {
            Text(text = "Book Now")
        }
    }
}