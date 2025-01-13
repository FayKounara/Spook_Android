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
import androidx.navigation.NavController

@Composable
fun BookingsScreen(userId: Int, userViewModel: UserViewModel, navController: NavController, viewModel: BookingViewModel, hour:String, persons:Int, storeId: String) {
    var customerName by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf(userId) }
    var enteredStoreId by remember { mutableStateOf(storeId) }
    var reservationDate by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(hour) }
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
                            userId = userId,
                            phoneNumber = "",
                            persons = 2,
                            occasion = ""
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
                BookingItem(navController, booking, persons, onDelete = { viewModel.deleteBooking(booking) })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BookingItem(navController: NavController, booking: Booking, persons: Int, onDelete: () -> Unit) {
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
                Text(text = "Name: ${booking.userId}")
                Text(text = "Date: ${booking.date}")
                Text(text = "Hours: ${booking.hours}")
                Text(text = "Store ID: ${booking.storeId}")
                Text(text = "Persons: $persons")
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