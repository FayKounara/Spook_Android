package com.example.room_setup_composables


import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_setup_composables.ui.theme.Screen


@Composable
fun ReviewNavigation(userId: Int, userViewModel:UserViewModel, reviewViewModel: ReviewViewModel, storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, slotViewModel: SlotViewModel, storeId: Int) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Reviews.route) {

        // Review Receive
        composable(route = Screen.Reviews.route) {
            ReviewScreen(navController, userId, userViewModel, reviewViewModel, storeId = storeId)
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
fun ReviewScreen(navController: NavController, userId: Int, userViewModel:UserViewModel, viewModel: ReviewViewModel, storeId: Int) {

    val reviews by viewModel.allReviews.collectAsState(initial = emptyList())
    val specificStoreReviews = reviews.filter { it.storeId == storeId }
    val users by userViewModel.allUsers.collectAsState(initial = emptyList())

    val currentUser = users.firstOrNull { it.userId == userId }
    val username = currentUser?.username ?: "Guest"

    BottomNavBar(
        onHomeClick = { navController.navigate(Screen.HomePage.withArgs(userId.toString())) },
        onProfileClick = { navController.navigate(Screen.ProfileScreen.withArgs(userId.toString())) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F4)) // Background to match Login Screen
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header with Store Title
            Spacer(modifier = Modifier.height(8.dp))
            ReviewHeader()

            Spacer(modifier = Modifier.height(12.dp))

            // Submit Review Section
            SubmitReview { reviewText, selectedStars ->
                viewModel.insertReview(
                    Review(
                        revId = 0,
                        stars = selectedStars,
                        revText = reviewText,
                        userId = userId,
                        storeId = storeId
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of Reviews
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize() // Εξασφάλιση ότι γεμίζει όλο το διαθέσιμο χώρο
            ) {
                if (specificStoreReviews.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize(), // Εξασφαλίζει ότι παίρνει όλο τον χώρο
                            contentAlignment = Alignment.Center // Κεντρική τοποθέτηση περιεχομένου
                        ) {
                            Text(
                                text = "No reviews yet",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                } else {
                    items(specificStoreReviews) { review ->
                        ReviewCard(
                            reviewText = review.revText,
                            reviewStars = review.stars.toString(),
                            reviewerName = username
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Coffee Cafe NYC",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}

@Composable
fun ReviewCard(reviewText: String, reviewStars: String, reviewerName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "⭐ $reviewStars",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFA726) // Stars with button color
                )
            )
            Text(
                text = reviewerName,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = reviewText,
            style = TextStyle(fontSize = 16.sp, color = Color.Black)
        )
    }
}

@Composable
fun SubmitReview(onReviewSubmit: (String, Int) -> Unit) {
    var reviewText by remember { mutableStateOf("") }
    var selectedStars by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Text
        Text(
            text = "Leave your review",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Review Text Field
        OutlinedTextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Write your review here...") },
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


        // Star Rating Section
        Text(
            text = "Rate your experience:",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            repeat(5) { index ->
                IconButton(
                    onClick = { selectedStars = index + 1 }
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star ${index + 1}",
                        tint = if (selectedStars > index) Color(0xFFFFA726) else Color.Gray
                    )
                }
            }
        }

        // Submit Button
        Button(
            onClick = {
                if (reviewText.isNotBlank() && selectedStars > 0) {
                    onReviewSubmit(reviewText, selectedStars)
                    reviewText = ""
                    selectedStars = 0
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
        ) {
            Text(
                text = "Submit Review",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
