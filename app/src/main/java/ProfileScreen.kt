import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.example.room_setup_composables.Booking
import com.example.room_setup_composables.BookingViewModel
import com.example.room_setup_composables.BottomNavBar
import com.example.room_setup_composables.HomePageNavigation
import com.example.room_setup_composables.LoginNavigation
import com.example.room_setup_composables.ReviewNavigation
import com.example.room_setup_composables.ReviewViewModel
import com.example.room_setup_composables.SlotViewModel
import com.example.room_setup_composables.StoreViewModel
import com.example.room_setup_composables.UserViewModel
import com.example.room_setup_composables.ui.theme.Screen

@Composable
fun ProfileNavigation(
    userId: Int,
    userViewModel: UserViewModel,
    storeViewModel: StoreViewModel,
    bookingViewModel: BookingViewModel,
    reviewViewModel: ReviewViewModel,
    slotViewModel: SlotViewModel
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ProfileScreen.route) {
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(
                navController,
                userId,
                userViewModel = userViewModel,
                bookingViewModel,
                storeViewModel
            )
            BottomNavBar(
                onHomeClick = {
                    navController.navigate(Screen.HomePage.withArgs(userId.toString())) },
                onProfileClick = {
                    navController.navigate(Screen.ProfileScreen.route) {
                        popUpTo(Screen.ProfileScreen.route) { inclusive = true }
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
fun ProfileScreen(
    navController: NavController,
    userId: Int,
    userViewModel: UserViewModel,
    bookingViewModel: BookingViewModel,
    storeViewModel: StoreViewModel
) {
    bookingViewModel.getBookingsForUser(userId)
    val bookingsHistory by bookingViewModel.bookings.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF1F3F4))
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        UserProfileSection(userViewModel, userId)


        BookingHeader()

        Spacer(modifier = Modifier.height(40.dp))

        BookingHistory(
            bookingsHistory = bookingsHistory,
            storeViewModel = storeViewModel,
            onCheckClick = { storeId ->
                navController.navigate(Screen.Reviews.withArgs(storeId.toString()))
            },
            navController = navController
        )

        Spacer(modifier = Modifier.height(12.dp))

        LogoutButton(
            onLogoutClick = {
                navController.navigate(Screen.LoginPage.withArgs())
            },
        )
    }

}

@Composable
fun BookingHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(
                color = Color(0xFFFFA726)
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Your bookings history",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun UserProfileSection(userViewModel: UserViewModel, userId: Int) {
    val userName by userViewModel.userDetails.collectAsState()
    val email by userViewModel.email.collectAsState()

    LaunchedEffect(userId) {

        userViewModel.fetchUserName(userId)
        userViewModel.fetchEmail(userId)
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp),
                    //.background(color = Color(0xFFF1F3F4)), shape = RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile Icon",
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(32.dp)
                )
                //Text(text = ".", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun BookingHistory(
    bookingsHistory: List<Booking>,
    storeViewModel: StoreViewModel,
    onCheckClick: (Int) -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (bookingsHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(
                        color = Color(0xFFF1F3F4),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No bookings available. Make your first reservation to see your history!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LogoutButton(
                        onLogoutClick = {
                            navController.navigate(Screen.LoginPage.withArgs())
                        },
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .padding(bottom = 60.dp)
            ) {

                items(bookingsHistory) { booking ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                8.dp,
                                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                            )
                            .background(color = Color.White)
                    ) {
                        BookingItem(
                            booking = booking,
                            storeViewModel = storeViewModel,
                            onCheckClick = onCheckClick,
                        )
                    }


                }
            }
        }
    }
}

@Composable
fun BookingItem(
    booking: Booking,
    storeViewModel: StoreViewModel,
    onCheckClick: (Int) -> Unit
) {

    val storeNames by storeViewModel.storeNames.collectAsState()
    val storeLocations by storeViewModel.storeLocations.collectAsState()

    val storeName = storeNames[booking.storeId] ?: "Loading..."
    val location = storeLocations[booking.storeId] ?: "Loading..."

    LaunchedEffect(booking.storeId) {
        Log.d("BookingItem", "Fetching data for : $storeName")
        storeViewModel.fetchStoreName(booking.storeId)
        storeViewModel.fetchStoreLocation(booking.storeId)
    }

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
                    //text = booking.storeName,
                    text="\uD83D\uDCCD $storeName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                   //text = booking.location,
                    text= " Address: $location",
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Text(
                    text = " Pax: ${booking.persons}",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                Text(
                    text = " Hour: ${booking.hours}",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
            Button(
                onClick = { onCheckClick(booking.storeId) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
            ) {
                Text(text = "Review", color = Color.White,fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LogoutButton(onLogoutClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onLogoutClick() },
            modifier = Modifier
                .width(150.dp)
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
        ) {
            Text(
                text = "Logout",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
