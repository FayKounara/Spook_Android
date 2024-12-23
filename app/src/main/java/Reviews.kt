package com.example.room_setup_composables


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

@Composable
fun ReviewScreen(navController: NavController, name: String?) {
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
            ReviewContent()
            BottomNavigationBar()
            PassedArgument(name)
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
            text = "Koffee Cafe NYC",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        )
    }
}

@Composable
fun ReviewContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        RatingBar()
        ReviewCard(
            reviewerName = "Courtney Henry",
            timeAgo = "2 mins ago",
            reviewText = "Consequat velit qui adipisicing sunt do reprehenderit ad laborum tempor ullamco exercitation."
        )
        ReviewCard(
            reviewerName = "Cameron Williamson",
            timeAgo = "5 mins ago",
            reviewText = "Ullamco tempor adipisicing et voluptate duis sit esse aliqua esse ex."
        )
        ReviewCard(
            reviewerName = "Jane Cooper",
            timeAgo = "10 mins ago",
            reviewText = "Ullamco tempor adipisicing et voluptate duis sit esse aliqua esse ex."
        )
    }
}

@Composable
fun RatingBar() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(5) { index ->
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
                    text = "${5 - index}",
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray)
                )
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(width)
                        .background(Color(0xFF007066), RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun ReviewCard(reviewerName: String, timeAgo: String, reviewText: String) {
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
                    text = reviewerName,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = timeAgo,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                )
            }
        }
        Text(
            text = reviewText,
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )
    }
}

@Composable
fun BottomNavigationBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF007066), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Write a Review",
            style = TextStyle(fontSize = 16.sp, color = Color.White)
        )
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
