package com.example.room_setup_composables.com.example.room_setup_composables.ui.theme

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_database_setup.R
import com.example.room_setup_composables.BookingViewModel
import com.example.room_setup_composables.BookingsScreen
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
fun StoreNavigation(userId: Int, userViewModel: UserViewModel, storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, filtername: String,filterday:String,slotViewModel: SlotViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Stores.route) {
        composable(route = Screen.Stores.route) {
            StoreList(navController, storeViewModel, filtername, slotViewModel ,filterday)
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
fun StoreList(
    navController: NavController,
    viewModel: StoreViewModel,
    filtername: String,
    slotViewModel: SlotViewModel,
    filterday: String
) {
    val stores by viewModel.allStores.collectAsState(initial = emptyList())
    val filteredStores = stores.filter { it.name == filtername }
    val slots by slotViewModel.slots.collectAsState(initial = emptyList())
    if (filteredStores.isNotEmpty()) {
        val store = filteredStores.first()
            // slotViewModel.fetchSlotsForStore(store.storeId)
        //val allAvailableHours = filteredStores.flatMap { it.avHours.split(",") }.distinct()
        //StoreCard(navController, store, allAvailableHours)
        //fay

        slotViewModel.fetchSlotsForStore1(store.storeId)


        val slotsForSelectedDay = slots.filter { it.storeId == store.storeId && it.day == filterday }
        if (slotsForSelectedDay.isNotEmpty()) {
            // Pass the filtered slots to your store card or whatever UI component you are rendering
            StoreCard(
                navController = navController,
                store = store,
                availableHours = slotsForSelectedDay
            )
        }
    }
}

@Composable
fun StoreCard(navController: NavController, store: Store, availableHours: List<Slot>) {
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

                // Δυναμική φόρτωση εικόνας βάσει του id του Store
                val imageRes = when (store.storeId) {
                    1 -> R.drawable.rest1photo
                    2 -> R.drawable.rest2photo
                    3 -> R.drawable.rest3photo
                    4 -> R.drawable.rest4photo
                    else -> R.drawable.rest1photo // Εικόνα προεπιλογής
                }

                Image(
                    painter = painterResource(id = imageRes), // Replace with actual image
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
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Αστεράκια για reviews
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable {
                        //navController.navigate("reviews/${store.storeId}")
                    }
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Review Star",
                        tint = Color(0xFFFFA726), // Χρώμα για τα αστεράκια
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = " Check Reviews",
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
                HourSelection(navController, availableHours, store.storeId.toString())
            }

            Spacer(modifier = Modifier.height(16.dp))
            StoreLocationToLatLng(store)

            Spacer(modifier = Modifier.height(16.dp))
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
            .padding(16.dp), // Add padding around the map
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
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = "10:30 AM - 11:00 PM",// Replace with actual opening hours
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = store.info,
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
//fun HourSelection(navController: NavController, availableHours: List<Slot>, storeId: String) {
//var selectedHour by remember { mutableStateOf<String>("00") }

//  Column {
//      availableHours.forEach { hour ->
//   val isDisabled = selectedHour != "00" && selectedHour != hour.toString().trim()

//   HourButton(hour = hour.toString().trim(),
//      isDisabled = isDisabled,
//     onClick = { selectedHour = hour.toString().trim() }
//     )
//   }

//    Spacer(modifier = Modifier.height(16.dp))

//      BookNowButton(
//        navController = navController,
//         storeId = storeId,
//         selectedHour = selectedHour
//       )
//   }
// }
fun HourSelection(navController: NavController, availableSlots: List<Slot>, storeId: String) {
    var selectedHour by remember { mutableStateOf<String>("00") }

    // Convert availableSlots from List<Slot> to List<String>
    val availableHours = availableSlots.map { it.hour } // Assuming Slot has a 'hour' property

    Column {
        availableHours.forEach { hour ->
            val isDisabled = selectedHour != "00"

            HourButton(
                hour = hour.toString(), // Directly using the string hour without trimming
                isDisabled = isDisabled,
                onClick = { selectedHour = hour.toString() } // Set selected hour directly
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Book Now Button
        BookNowButton(
            navController = navController,
            storeId = storeId,
            selectedHour = selectedHour
        )
    }
}



@Composable
fun HourButton(hour: String, isDisabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
        enabled = !isDisabled // Disable the button if not clickable
    ) {
        Text(text = hour)
    }
}

@Composable
fun BookNowButton(navController: NavController, storeId: String, selectedHour: String) {
    Button(
        onClick = { navController.navigate(Screen.Bookings.withArgs(selectedHour, storeId)) },
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
