package com.example.room_setup_composables


//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



// Dummy data and composable functions for demo purposes
val sampleOffers = listOf(
    Offer(0,"Pizza","Pizza Margarita", 15.99, 12.99,0),
    Offer(1,"Burger","Double Smashed Burger",10.99, 8.99, 1 ),
    Offer(3,"Pasta", "Bolognese",13.99, 10.99,2 )
)

val sampleRestaurants = listOf(
    Store(0, "Downtown Square","","","",""),
    Store(1, "Elm Street","","","",""),
    Store(2, "Beach Avenue","","","","")
)

@Composable
fun Homepage(name: String, modifier: Modifier = Modifier) {
    // Scrollable container
    Box(
        modifier = modifier // Χρήση του modifier που περνάει από την κλήση
            .fillMaxSize()
            .background(color = Color(0xFFF1F3F4))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Welcome back $name!",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                //Image (
                //    painter = painterResource(id = R.drawable.profile_placeholder),
                //    contentDescription = "Profile Image",
                //    modifier = Modifier
                //        .size(40.dp)
                //        .clip(androidx.compose.foundation.shape.CircleShape)
                //)
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFE8E8E8))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Search your today's table",
                    color = Color.Gray,
                    modifier = Modifier.padding(4.dp)
                )
            }

            // Today's Offers Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Today's Offers!",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dummy data for today's offers
                    items(sampleOffers) { item ->
                        FoodCard(foodItem = item)
                    }
                }
            }

            // Explore Restaurants Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Explore Our Restaurants",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Today's Availability",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 14.sp)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleRestaurants) { store ->
                        RestaurantCard(
                            store = store,
                            onBookClick = {
                                // Ενέργεια για το κουμπί "Book"
                                println("Booked table at ${store.name}")
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FoodCard(foodItem: Offer) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Καθορίζει το φόντο της κάρτας
        ),
        modifier = Modifier
            .width(200.dp)
            .padding(end = 16.dp)
    ) {
        Column {
            //Image(
            //  painter = painterResource(id = foodItem.imageResId),
            //   contentDescription = "Food Item",
            //   modifier = Modifier
            //        .fillMaxWidth()
            //        .height(120.dp),
            //  contentScale = androidx.compose.ui.layout.ContentScale.Crop
            //)
            Column(
                modifier = Modifier
                    .padding(8.dp), // Απόσταση γύρω από το κείμενο
                verticalArrangement = Arrangement.spacedBy(6.dp), // Απόσταση μεταξύ των στοιχείων
                horizontalAlignment = Alignment.Start // Ευθυγράμμιση στην αριστερή πλευρά
            ) {
                Text(
                    text = foodItem.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(1.dp)) // Κενό ανάμεσα στο όνομα και την τιμή/τοποθεσία
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Μικρότερη απόσταση μεταξύ τιμής και τοποθεσίας
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = foodItem.orgPrice.toString(),//**************************
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.Gray,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough // Διαγράμμιση αρχικής τιμής
                            ),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = foodItem.discountPrice.toString(),//*****************
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFA726) // Πορτοκαλί χρώμα
                            )
                        )
                    }
                    Text(
                        text = foodItem.storeId.toString(),//********************
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Light)
                    )
                }
            }
        }
    }
}


@Composable
fun RestaurantCard(store: Store, onBookClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Εικόνα Εστιατορίου
            //Image(
            //   painter = painterResource(id = store.imageResId),
            //  contentDescription = "Restaurant Image",
            //modifier = Modifier
            //  .size(80.dp)
            //.clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
            // contentScale = androidx.compose.ui.layout.ContentScale.Crop
            //)

            // Στήλη για Όνομα και Τοποθεσία
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = store.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = store.location,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Κουμπί "Book"
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp, minWidth = 80.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Book")
            }
        }
    }
}