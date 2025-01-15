package com.example.room_setup_composables.com.example.room_setup_composables.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_database_setup.R
import com.example.room_setup_composables.BookingNavigation
import com.example.room_setup_composables.BookingViewModel
import com.example.room_setup_composables.BookingsScreen
import com.example.room_setup_composables.BottomNavBar
import com.example.room_setup_composables.HomePageNavigation
import com.example.room_setup_composables.ProfileNavigation
import com.example.room_setup_composables.Review
import com.example.room_setup_composables.ReviewNavigation
import com.example.room_setup_composables.ReviewScreen
import com.example.room_setup_composables.ReviewViewModel
import com.example.room_setup_composables.Slot
import com.example.room_setup_composables.SlotViewModel
import com.example.room_setup_composables.Store
import com.example.room_setup_composables.StoreViewModel
import com.example.room_setup_composables.UserViewModel
import com.example.room_setup_composables.ui.theme.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

@Composable
fun StoreNavigation(userId: Int, userViewModel: UserViewModel, storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, reviewViewModel: ReviewViewModel, filtername: String, filterday: String, persons: Int, slotViewModel: SlotViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Stores.route) {

        // Store Receive
        composable(route = Screen.Stores.route) {
            StoreList(navController, storeViewModel, reviewViewModel, slotViewModel, filtername, filterday)
            BottomNavBar(
                onHomeClick = { navController.navigate(Screen.HomePage.withArgs(userId.toString())) },
                onProfileClick = { navController.navigate(Screen.ProfileScreen.withArgs(userId.toString())) }
            )
        }

        // Navigation to Bookings
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
            BookingNavigation(userId, userViewModel, bookingViewModel, hour, filterday, filtername, persons, storeId, reviewViewModel, storeViewModel, slotViewModel)
        }

        // Navigation to reviews
        composable(
            route = Screen.Reviews.route + "/{storeId}",
            arguments = listOf(
                navArgument("storeId") {
                    type = NavType.IntType
                    defaultValue = 1
                    nullable = false
                }
            )
        ) { entry ->
            val storeId = entry.arguments?.getInt("storeId") ?: 1
            ReviewNavigation(userId, userViewModel, reviewViewModel, storeViewModel, bookingViewModel, slotViewModel, storeId = storeId)
        }

        // Navigation to HomePage
        composable(
            route = Screen.HomePage.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    defaultValue = "1"
                    nullable = true
                }
            )
        ) { entry ->
            HomePageNavigation(userId = userId, userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
        }

        // Navigation to profile
         composable(
            route = Screen.ProfileScreen.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = 1
                    nullable = false
                }
            )
        ) { entry ->
            ProfileNavigation(userId, userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
        }
    }
}

@Composable
fun StoreList(
    navController: NavController,
    viewModel: StoreViewModel,
    reviewViewModel: ReviewViewModel,
    slotViewModel: SlotViewModel,
    filtername: String,
    filterday: String
) {
    val allReviews by reviewViewModel.allReviews.collectAsState(initial = emptyList())
    val stores by viewModel.allStores.collectAsState(initial = emptyList())
    val filteredStores = stores.filter { it.name == filtername }
    val slots by slotViewModel.slots.collectAsState(initial = emptyList())

    if (filteredStores.isNotEmpty()) {
        val store = filteredStores.first()
        val storeReviews = allReviews.filter { it.storeId == store.storeId }

        slotViewModel.fetchSlotsForStore1(store.storeId)

        val slotsForSelectedDay = slots.filter { it.storeId == store.storeId && it.day == filterday }
        if (slotsForSelectedDay.isNotEmpty()) {
            // Pass the filtered slots to your store card or whatever UI component you are rendering
            StoreCard(
                navController = navController,
                store = store,
                storeReviews = storeReviews,
                availableHours = slotsForSelectedDay,
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StoreCard(navController: NavController, store: Store, storeReviews: List<Review>, availableHours: List<Slot>) {
    val sumOfReviews = storeReviews.size
    val sumOfStars = storeReviews.sumOf { it.stars }
    val rating = if (sumOfReviews > 0) {
        (sumOfStars.toFloat() / sumOfReviews).let { String.format("%.1f", it).toFloat() }
    } else {
        0
    }

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFF1F3F4)), // Background για όλη την κάρτα
        colors = CardDefaults.cardColors(containerColor = Color.White) // Η κάρτα να είναι λευκή
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState()) // Προσθήκη Scroll
        ) {
            // Προσθήκη Εικόνας
            Image(
                painter = painterResource(id = R.drawable.restphoto), 
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

            // Αστεράκια για reviews
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable {
                        navController.navigate(Screen.Reviews.withArgs(store.storeId.toString()))
                    }
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Review Star",
                        tint = if (index < rating.toInt()) Color(0xFFFFA726) else Color.Gray, // Filled and empty star colors
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "$rating Check Reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp),
                    color = Color.Gray
                )
            }

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
                HourSelection(navController, store, availableHours)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Περισσότερο ύψος για τον χάρτη
            ) {
                StoreLocationToLatLng(store)
            }
        }
    }
}

//map
@Composable
fun StoreMap(latitude: Double, longitude: Double) {

    val location = LatLng(latitude, longitude)


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f) // Zoom level 15 for a close view
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize() // Make the map take up the full available space
            .padding(12.dp), // Add padding around the map
        cameraPositionState = cameraPositionState
    ) {
        val markerState = MarkerState(position = location)
        Marker(
            state = markerState,
            title = "Store Location",
            snippet = "This is the location of the store."
        )
    }
}

fun getLatLngFromAddress(context: Context, address: String): Pair<Double, Double>? {
    val geocoder = Geocoder(context, Locale.getDefault()) // Create Geocoder instance
    try {
        // Get the list of addresses based on the address string
        val addressResult: Address? = geocoder.getFromLocationName(address, 1)?.firstOrNull()

        // If a valid address result is found, return its latitude and longitude
        addressResult?.let {
            return Pair(it.latitude, it.longitude)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null // Return null if geocoding fails or no address is found
}

@Composable
fun StoreLocationToLatLng(store: Store) {
    val context = LocalContext.current
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }

    // When the store location changes, fetch the latitude and longitude
    LaunchedEffect(store.location) {
        val latLng = getLatLngFromAddress(context, store.location)
        if (latLng != null) {
            latitude = latLng.first  // Update latitude
            longitude = latLng.second // Update longitude
            isLoading = false
        } else {
            Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    // Show loading indicator while fetching location
    if (isLoading) {
        CircularProgressIndicator() // Show a loading indicator while fetching coordinates
    } else if (latitude != 0.0 && longitude != 0.0) {
        // Only show the map when valid coordinates are available
        StoreMap(latitude, longitude)
    } else {
        // Handle case where coordinates are not found (e.g., show an error message or default view)
        Text("Invalid location.")
    }
}


//map
@Composable
fun StoreDetailsSection(store: Store) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = store.info,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun HourSelection(navController: NavController, store: Store, availableSlots: List<Slot>) {
    var selectedHour by remember { mutableStateOf<Int?>(null) }

    // Αν τα διαθέσιμα slots είναι Int
    val availableHours = availableSlots.map { it.hour.toInt() } // Μετατροπή των hours σε Int, αν χρειαστεί

    Column {
        // Προσθήκη οριζόντιας στοίχισης και κύλισης
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Αφή μεταξύ των κουμπιών
        ) {
            availableHours.forEach { hour ->
                HourButton(
                    hour = hour.toString(), // Εμφανίζεται ως string για το UI
                    isSelected = selectedHour == hour, // Σύγκριση Int με Int
                    onClick = { selectedHour = hour } // Ενημερώνουμε το επιλεγμένο slot
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Book Now Button
        BookNowButton(
            navController = navController,
            store = store,
            selectedHour = selectedHour
        )
    }
}

@Composable
fun HourButton(hour: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Gray else Color(0xFFFFA726)
        ),
        enabled = true
    ) {
        Text(text = hour)
    }
}

@Composable
fun BookNowButton(navController: NavController, store: Store, selectedHour: Int?) {
    Button(
        onClick = {
            selectedHour?.let { hour ->
                navController.navigate(Screen.Bookings.withArgs( hour.toString(), store.storeId.toString()) )// Χρησιμοποιούμε hour ως String
            }
        },
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



