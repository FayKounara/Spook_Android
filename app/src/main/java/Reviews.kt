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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults


@Composable
fun ReviewScreen(navController: NavController, viewModel: ReviewViewModel, storeId: Int) {
    val reviews by viewModel.allReviews.collectAsState(initial = emptyList())
    val specificStoreReviews = reviews.filter { it.storeId == storeId }

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
            ReviewHeader()

            // Submit Review Section
            SubmitReview { reviewText, selectedStars ->
                viewModel.insertReview(
                    Review(
                        revId = 0,
                        stars = selectedStars,
                        revText = reviewText,
                        userId = 1, // Replace with actual user ID
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
                            reviewerName = "User ${review.userId}"
                        )
                    }
                }
            }


            PassedArgument(storeId.toString())

            // Back Button
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back",
                    tint = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFFFA726), RoundedCornerShape(50)) // Button color
                        .padding(12.dp)
                )
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
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        TextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Write your review") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Star Rating Selection
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                IconButton(onClick = { selectedStars = index + 1 }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star ${index + 1}",
                        tint = if (selectedStars > index) Color(0xFFFFA726) else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onReviewSubmit(reviewText, selectedStars)
                reviewText = ""
                selectedStars = 0
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)) // Orange Button
        ) {
            Text(
                text = "Submit Review",
                style = TextStyle(color = Color.White, fontSize = 16.sp)
            )
        }
    }
}


@Composable
fun PassedArgument(name: String?) {

        Text(
            text = "Hi again, $name",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        )

}
