package com.example.room_setup_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import com.example.room_setup_composables.ui.theme.Screen

@Composable
fun BookingNavigation(userId: Int, userViewModel: UserViewModel, bookingViewModel: BookingViewModel, hour:String, persons:Int, storeId: String, reviewViewModel: ReviewViewModel, storeViewModel: StoreViewModel, slotViewModel: SlotViewModel) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Bookings.route) {

        // Review Receive
        composable(route = Screen.Bookings.route) {
            BookingsScreen(userId, userViewModel, navController, bookingViewModel, hour, persons, storeId, slotViewModel)
            BottomNavBar(
                onHomeClick = { navController.navigate(Screen.HomePage.withArgs(userId.toString())) },
                onProfileClick = { navController.navigate(Screen.ProfileScreen.withArgs(userId.toString())) }
            )
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
        ) { _ ->
            ProfileNavigation(userId, userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
        }
    }
}

@Composable
fun BookingsScreen(userId: Int, userViewModel: UserViewModel, navController: NavController, viewModel: BookingViewModel, hour:String, persons:Int, storeId: String, slotViewModel: SlotViewModel) {
    //val primaryColor = Color(0xFFFFA000)
    var username by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf(userId) }
    var enteredStoreId by remember { mutableStateOf(storeId) }
    var phoneNumber by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(hour) }

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
        else -> R.drawable.burgerphoto // Προεπιλεγμένη εικόνα
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())


    ) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.padding(8.dp).border(2.dp, Color(0xFFFF9800), shape = RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    text = "Yum! \n You are one step closer to your booking @",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center

                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = viewModel.storeName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFF9800),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = foodImage),
                    contentDescription = "Food Item",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\uD83D\uDD52 Don't be late! Your reservation is at $hour pm sharp.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)

                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = {
                        Text(
                            "\uD83D\uDD8A\uFE0F Fill out the name for the reservation...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFA726),
                        unfocusedBorderColor = Color(0xFFBDBDBD),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), shape = RoundedCornerShape(8.dp))
                        .padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = {
                        Text(
                            "\uD83D\uDCDE ... and your phone number. Just in case!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    textStyle = TextStyle(fontSize = 16.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFA726),
                        unfocusedBorderColor = Color(0xFFBDBDBD),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), shape = RoundedCornerShape(8.dp))
                        .padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))


                Button(
                    onClick = {
                        if (username.isNotEmpty() && phoneNumber.isNotEmpty() &&
                            hour.isNotEmpty() && enteredStoreId.isNotEmpty()
                        ) {
                            viewModel.insertBooking(
                                Booking(
                                    date = "",
                                    hours = hour,
                                    storeId = enteredStoreId.toInt(),
                                    userId = userId,
                                    phoneNumber = phoneNumber,
                                    persons = 2,
                                    occasion = ""
                                )
                            )
                            slotViewModel.reduceSlotAvailability(enteredStoreId.toInt(), hour)
                            username = ""
                            phoneNumber = ""
                            hour = ""
                            enteredStoreId = ""
                            showReservationComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137316)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Reserve now", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

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
                            text = "Your reservation was successfully made! Enjoy your time, and don't forget to rate your experience!",
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
                            Text(
                                text = "OK",
                                color = Color(0xFFFF9800)
                            )
                        }
                    },
                    containerColor = Color.White,

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
}