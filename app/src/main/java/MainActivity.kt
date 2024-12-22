package com.example.room_setup_composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.room_setup_composables.ui.theme.RoomDatabaseSetupTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoomDatabaseSetupTheme {
                BookingScreen(viewModel)
            }
        }
    }
}

@Composable
fun BookingScreen(viewModel: BookingViewModel) {
    var customerName by remember { mutableStateOf("") }
    var reservationDate by remember { mutableStateOf("") }
    //val bookings by viewModel.allBookings.observeAsState(emptyList())
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
        Button(
            onClick = {
                if (customerName.isNotEmpty() && reservationDate.isNotEmpty()) {
                    viewModel.insertBooking(
                        Booking(
                            customerName = customerName,
                            reservationDate = reservationDate
                        )
                    )
                    customerName = ""
                    reservationDate = ""
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
                BookingItem(booking, onDelete = { viewModel.deleteBooking(booking) })
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking, onDelete: () -> Unit) {
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
                Text(text = "Name: ${booking.customerName}")
                Text(text = "Date: ${booking.reservationDate}")
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
