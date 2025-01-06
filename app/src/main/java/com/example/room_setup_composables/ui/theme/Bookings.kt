package com.example.room_setup_composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.room_setup_composables.ui.theme.Screen


@Composable
fun BookingNavigation(viewModel: BookingViewModel, name: String) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Bookings.route) {
        composable(route = Screen.Bookings.route + "/{hour}/{storeId}",
            arguments = listOf(navArgument("hour") { type = NavType.StringType },navArgument("storeId") {type = NavType.IntType}) )
        {entry ->
            val hour = entry.arguments?.getString("hour") ?: ""
            val storeId = entry.arguments?.getInt("storeId") ?: ""
            BookingsScreen(navController, viewModel, hour.toString(),  storeId.toString())

        }
    }
}

@Composable
fun BookingsScreen(navController: NavController, viewModel: BookingViewModel, hour:String, storeId: String) {
    var customerName by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var enteredStoreId by remember { mutableStateOf(storeId) }
    var reservationDate by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(hour) }
    //var storeId by remember { mutableStateOf("") }
    val bookings by viewModel.allBookings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("Enter Customer Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = reservationDate,
            onValueChange = { reservationDate = it },
            label = { Text("Enter Reservation Date") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = hour,
            onValueChange = { hour = it },
            label = { Text("Enter Reservation Hours") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = enteredStoreId,
            onValueChange = { enteredStoreId = it },
            label = { Text("Enter Store ID") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (customerName.isNotEmpty() && reservationDate.isNotEmpty() &&
                    hour.isNotEmpty() && enteredStoreId.isNotEmpty()
                ) {
                    viewModel.insertBooking(
                        Booking(
                            date = reservationDate,
                            hours = hour,
                            storeId = enteredStoreId.toInt(),
                            userId = customerName.toInt(),
                            phoneNumber = "",
                            persons = 2,
                            occasion = "" // Replace with real user ID logic
                        )
                    )
                    customerName = ""
                    reservationDate = ""
                    hour = ""
                    enteredStoreId = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Booking")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        LazyColumn {
            items(bookings) { booking ->
                BookingItem(navController, booking, onDelete = { viewModel.deleteBooking(booking) })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BookingItem(navController: NavController, booking: Booking, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Name: ${booking.userId}") // Replace with logic to fetch name from userId
                Text(text = "Date: ${booking.date}")
                Text(text = "Hours: ${booking.hours}")
                Text(text = "Store ID: ${booking.storeId}")
            }
            IconButton(onClick = { onDelete() }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Booking"
                )
            }
        }
    }
}