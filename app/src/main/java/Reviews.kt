package com.example.room_setup_composables


import androidx.compose.runtime.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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



@Composable
fun ReviewScreen(navController: NavController, viewModel: ReviewViewModel, storeId: Int) {

    // Collect the list of reviews as state
    val reviews by viewModel.allReviews.collectAsState(initial = emptyList())

    // Filter the reviews to get all reviews from the specific store
    val specificStoreReviews = reviews.filter { it.storeId == storeId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp), // Add padding to the bottom so the button won't overlap content
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ReviewHeader()
            SubmitReview { reviewText, selectedStars ->
                // Handle review submission here
                viewModel.insertReview(Review(revId = 0, selectedStars, reviewText, userId = 1, storeId)) // Replace `123` with actual user ID
            }
            ReviewContent(specificStoreReviews)
            PassedArgument(storeId.toString())
        }

        // Go Back button at the bottom of the screen
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Aligning the button at the bottom center
                .padding(16.dp) // Padding for the button
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Go Back",
                modifier = Modifier
                    .background(Color(0xFF007066), RoundedCornerShape(50))
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun ReviewHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Coffee Cafe NYC",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        )
    }
}

@Composable
fun ReviewContent(specificStoreReviews: List<Review>) {
    if (specificStoreReviews.isEmpty()) {
        // Show a message when no reviews are available
        Text(
            text = "No reviews yet",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
//            modifier = Modifier.align(Alignment.Center)
        )
    } else {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                RatingBar(specificStoreReviews)
            }

            itemsIndexed(specificStoreReviews) { index, review ->
                ReviewCard(
                    reviewText = review.revText,
                    reviewStars = review.stars.toString(),
                    reviewerName = review.userId.toString()
                )
            }
        }
    }
}

@Composable
fun RatingBar(specificStoreReviews: List<Review>) {

    val starCounts = remember(specificStoreReviews) {
        List(5) { star -> specificStoreReviews.count { it.stars == star + 1 } }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(5) { index ->
            val starRating = 5 - index
            val count = starCounts[4 - index]
            val width = when (index) {
                0 -> 150.dp // 5-star rating width
                1 -> 100.dp // 4-star rating width
                2 -> 60.dp  // 3-star rating width
                3 -> 30.dp  // 2-star rating width
                else -> 10.dp // 1-star rating width
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$starRating Stars",
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                )
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(width)
                        .background(Color(0xFF007066), RoundedCornerShape(4.dp))
                )
                Text(
                    text = "($count)",  // Display the count of reviews for this rating
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                )
            }
        }
    }
}

@Composable
fun ReviewCard(reviewText: String, reviewStars: String, reviewerName: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = reviewText,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = reviewStars,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
            }
        }
        Text(
            text = reviewerName,
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )
    }
}

@Composable
fun SubmitReview(
    onReviewSubmit: (String, Int) -> Unit // Callback to submit the review text and stars
) {
    var reviewText by remember { mutableStateOf("") }
    var selectedStars by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Increased height for the form
            .background(Color(0xFF007066), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Review Text Input
            TextField(
                value = reviewText,
                onValueChange = {
                    reviewText = it
                },
                label = { Text("Write your review") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 16.sp),
                maxLines = 3 // Allow multiple lines for review text
            )

            // Star Rating
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { selectedStars = index + 1 },
//                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star ${index + 1}",
                            tint = if (selectedStars > index) Color.Yellow else Color.Gray
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = { onReviewSubmit(reviewText, selectedStars) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Submit Review",
                    style = TextStyle(fontSize = 16.sp, color = Color.White)
                )
            }
        }
    }
}


@Composable
fun PassedArgument(name:String?) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Hello, $name")
    }
}