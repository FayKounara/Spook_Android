package com.example.room_setup_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_database_setup.R
import com.example.room_setup_composables.com.example.room_setup_composables.ui.theme.StoreNavigation
import com.example.room_setup_composables.ui.theme.Screen
import kotlinx.coroutines.delay

@Composable
fun BookingNavigation(userId: Int, userViewModel: UserViewModel, bookingViewModel: BookingViewModel, hour: String, filterday: String, filtername: String, persons: Int, storeId: String, reviewViewModel: ReviewViewModel, storeViewModel: StoreViewModel, slotViewModel: SlotViewModel) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Bookings.route) {

        // Bookings Receive
        composable(route = Screen.Bookings.route) {
            BookingsScreen(userId, userViewModel, navController, bookingViewModel, hour, persons, storeId, slotViewModel)
            BottomNavBar(
                onHomeClick = { navController.navigate(Screen.HomePage.withArgs(userId.toString())) },
                onProfileClick = { navController.navigate(Screen.ProfileScreen.withArgs(userId.toString())) }
            )
        }


        composable(
            route = Screen.Stores.route + "/{name}",
            arguments = listOf(

                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            StoreNavigation(userId, userViewModel, storeViewModel, bookingViewModel, reviewViewModel, filtername = filtername, filterday, persons, slotViewModel)
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
        ) { _ ->
            HomePageNavigation(userId = userId, userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
        }

        composable(
            route = Screen.ProfileScreen.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = 1
                    nullable = false
                }
            )
        ) { _ ->
            ProfileNavigation(userId, userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
        }
    }
}

@Composable
fun BookingsScreen(
    userId: Int,
    userViewModel: UserViewModel,
    navController: NavController,
    viewModel: BookingViewModel,
    hour: String,
    persons: Int,
    storeId: String,
    slotViewModel: SlotViewModel
) {
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val users by userViewModel.allUsers.collectAsState(initial = emptyList())
    val currentUser = users.firstOrNull { it.userId == userId }
    username = currentUser?.username ?: "Guest"
    phoneNumber = currentUser?.phoneNumber ?: "6900000000"

    var showReservationComplete by remember { mutableStateOf(false) }

    viewModel.newEnteredStoreId(storeId)

    val foodImage = when {
        viewModel.storeName.contains("Pizza", ignoreCase = true) -> R.drawable.pizzaphoto
        viewModel.storeName.contains("Burger", ignoreCase = true) -> R.drawable.burgerphoto
        viewModel.storeName.contains("Pasta", ignoreCase = true) -> R.drawable.pastaphoto
        else -> R.drawable.burgerphoto
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFFDFDFD))
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Yum! You are one step closer to your booking at",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF212121),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = viewModel.storeName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFF9800),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = foodImage),
                    contentDescription = "Food Item",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\uD83D\uDD52 Reservation time: $hour pm",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Name for the reservation", color = Color(0xFF757575), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFA726),
                        unfocusedBorderColor = Color(0xFFBDBDBD)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone number", color = Color(0xFF757575), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFA726),
                        unfocusedBorderColor = Color(0xFFBDBDBD)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (username.isNotEmpty() && phoneNumber.isNotEmpty() && hour.isNotEmpty() && storeId.isNotEmpty()) {
                            viewModel.insertBooking(
                                Booking(
                                    date = "",
                                    hours = hour,
                                    storeId = storeId.toInt(),
                                    userId = userId,
                                    phoneNumber = phoneNumber,
                                    persons = persons,
                                    occasion = ""
                                )
                            )
                            repeat(persons / 2) {
                                slotViewModel.reduceSlotAvailability(storeId.toInt(), hour)
                            }
                            showReservationComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reserve now", fontSize = 18.sp, color = Color.White)
                }
            }
        }

        if (showReservationComplete) {
            AlertDialog(
                onDismissRequest = { showReservationComplete = false },
                title = {
                    Text(
                        text = "Reservation Complete \uD83C\uDF89",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFF9800)
                    )
                },
                text = {
                    Text(
                        text = "Your reservation was successfully made! Enjoy your juicy meal!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.DarkGray
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showReservationComplete = false
                            navController.navigate(Screen.ProfileScreen.withArgs(userId.toString()))
                        }
                    ) {
                        Text("OK", color = Color(0xFFFF9800))
                    }
                },
                containerColor = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        IconButton(
            onClick = { navController.navigate(Screen.Stores.withArgs(userId.toString())) },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFFFF9800)
            )
        }
    }
}




