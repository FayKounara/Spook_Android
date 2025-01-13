package com.example.room_setup_composables


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_setup_composables.ui.theme.Screen

// Μετονομασία της κλάσης από Booking σε BookingsHistory
data class BookingsHistory(val storeId: Int, val storeName: String, val date: String, val location: String)

@Composable
fun ProfileNavigation(
    userId: Int,
    userViewModel:UserViewModel,
    storeViewModel: StoreViewModel,
    bookingViewModel: BookingViewModel,
    reviewViewModel: ReviewViewModel,
    slotViewModel: SlotViewModel
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ProfileScreen.route) {
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(navController, userId, userViewModel = userViewModel, bookingViewModel, slotViewModel, reviewViewModel)
            BottomNavBar(
                onHomeClick = { navController.navigate(Screen.HomePage.withArgs(userId.toString())) },
                onProfileClick = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        popUpTo(Screen.ProfileScreen.route) { inclusive = true } // Avoids stacking multiple Profile pages
                    }
                }
            )
        }

        // For navigation to reviews
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

        composable(
            route = Screen.LoginPage.route,
            arguments = emptyList()
        ) { _ ->
            LoginNavigation(userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
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
    }
}


@Composable
fun ProfileScreen(navController: NavController, userId: Int, userViewModel: UserViewModel, bookingViewModel: BookingViewModel , slotViewModel: SlotViewModel, reviewViewModel: ReviewViewModel) {

    val bookingsHistory = listOf(
        BookingsHistory(storeId = 1, storeName = "Ambrosia Hotel & Restaurant", date = "2025-01-01", location = "Kazi Deiry, Taiger Pass, Chittagong"),
        BookingsHistory(storeId = 2, storeName = "Tava Restaurant", date = "2025-01-02", location = "Zakir Hossain Rd, Chittagong"),
        BookingsHistory(storeId = 3, storeName = "Haatkhola", date = "2025-01-03", location = "6 Surson Road, Chittagong")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF1F3F4))
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        UserProfileSection()

        BookingHeader()

        Spacer(modifier = Modifier.height(40.dp))

        BookingHistory(
            bookingsHistory = bookingsHistory,
            onCheckClick = { storeId ->
                println("Selected Store ID: $storeId")
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        LogoutButton(navController, onLogoutClick = {
            navController.navigate(Screen.LoginPage.withArgs())
        },)
    }

}

@Composable
fun BookingHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFFFA726),
                shape = RoundedCornerShape(
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bookings History",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun UserProfileSection() {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color = Color(0xFFF1F3F4)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = ".", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sadek Hossen",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "sadekbranding@gmail.com",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun BookingHistory(
    bookingsHistory: List<BookingsHistory>,
    onCheckClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(bookingsHistory) { booking ->
                BookingItem(
                    booking = booking,
                    onCheckClick = onCheckClick
                )
            }
        }
    }
}

@Composable
fun BookingItem(
    booking: BookingsHistory,
    onCheckClick: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.storeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = booking.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Date: ${booking.date}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Button(
                onClick = { onCheckClick(booking.storeId) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
            ) {
                Text(text = "Review", color = Color.White)
            }
        }
    }
}

@Composable
fun LogoutButton(navController: NavController, onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onLogoutClick() },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
        ) {
            Text(
                text = "Logout",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
