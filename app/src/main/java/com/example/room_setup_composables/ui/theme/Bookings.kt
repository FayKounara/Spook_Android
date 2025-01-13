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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BookingsScreen(userId: Int, userViewModel: UserViewModel, navController: NavController, viewModel: BookingViewModel, hour:String, persons:Int, storeId: String, slotViewModel: SlotViewModel) {
    val primaryColor = Color(0xFFFFA000)
    var customerName by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf(userId) }
    var enteredStoreId by remember { mutableStateOf(storeId) }
    var phone by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(hour) }
    val bookings by viewModel.allBookings.collectAsState()
    //val name by viewModel.selectedStoreName.collectAsState()

    var showReservationComplete by remember { mutableStateOf(false) }

    viewModel.newEnteredStoreId(storeId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {


                Text(text = "One step closer to your booking!")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Your name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Your phone number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = hour,
                    onValueChange = { hour = it },
                    label = { Text("Be there at: "); hour.toString() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    readOnly = true

                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = viewModel.storeName,
                    onValueChange = {},
                    label = { Text("You are going to...") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (customerName.isNotEmpty() && phone.isNotEmpty() &&
                            hour.isNotEmpty() && enteredStoreId.isNotEmpty()
                        ) {
                            viewModel.insertBooking(
                                Booking(
                                    date = "",
                                    hours = hour,
                                    storeId = enteredStoreId.toInt(),
                                    userId = userId,
                                    phoneNumber = phone,
                                    persons = 2,
                                    occasion = ""
                                )
                            )
                            slotViewModel.reduceSlotAvailability(enteredStoreId.toInt(), hour)
                            customerName = ""
                            phone = ""
                            hour = ""
                            enteredStoreId = ""
                            showReservationComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Reserve now")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))


            if (showReservationComplete) {
                AlertDialog(
                    onDismissRequest = { showReservationComplete = false },
                    title = { Text("Reservation Complete") },
                    text = { Text("👍 Your reservation was successfully made!") },
                    confirmButton = {
                        TextButton(onClick = {
                            showReservationComplete = false; navController.navigateUp()
                        }) {
                            Text("OK")
                        }
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))
//            LazyColumn {
//                items(bookings) { booking ->
//                    BookingItem(
//                        navController,
//                        booking,
//                        persons,
//                        onDelete = { viewModel.deleteBooking(booking) })
//                }
//            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }



    @Composable
    fun BookingItem(
        navController: NavController,
        booking: Booking,
        persons: Int,
        onDelete: () -> Unit
    ) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column {
//                Text(text = "Name: ${booking.userId}")
//                //Text(text = "Date: ${booking.date}")
//                Text(text = "Hours: ${booking.hours}")
//                Text(text = "Store ID: ${booking.storeId}")
//                Text(text = "Persons: $persons")
//            }
//            IconButton(onClick = { onDelete() }) {
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Delete Booking"
//                )
//            }
//        }
//    }
    }
}