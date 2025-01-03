package com.example.room_setup_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_setup_composables.com.example.room_setup_composables.ui.theme.StoreNavigation
import com.example.room_setup_composables.ui.theme.Screen


// Dummy data and composable functions for demo purposes
val sampleOffers = listOf(
    Offer(0,"Pizza","Pizza Margarita", 15.99, 12.99,"a", 0),
    Offer(1,"Burger","Double Smashed Burger",10.99, 8.99, "b", 1),
    Offer(3,"Pasta", "Bolognese",13.99, 10.99,"c", 2)
)

/*val sampleRestaurants = listOf(
    Store(0, "Downtown Square","","","",""),
    Store(1, "Elm Street","","","",""),
    Store(2, "Beach Avenue","","","","")
)*/


@Composable
fun HomePageNavigation(storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, reviewViewModel: ReviewViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.HomePage.route) {
        composable(route = Screen.HomePage.route) {
            Homepage(navController, "John", storeViewModel = storeViewModel)
        }
        composable(
            route = Screen.Stores.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "John"
                    nullable = true
                }
            )
        ) { entry ->
            val name = entry.arguments?.getString("name") ?: "Juicy Grill"
            StoreNavigation(storeViewModel, bookingViewModel, name)
        }

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
            ReviewScreen(navController, reviewViewModel, storeId = storeId)
        }
    }
}


@Composable
fun Homepage(navController: NavController, name: String, storeViewModel: StoreViewModel, modifier: Modifier = Modifier) {
    val stores by storeViewModel.allStores.collectAsState(initial = emptyList())
    var selectedDay by remember { mutableStateOf("Monday") }

    // Φιλτράρισμα με βάση την επιλεγμένη ημέρα
    val filteredStores = stores.filter { store ->
        store.avDays.split(",").contains(selectedDay)
    }

    Box(
        modifier = modifier
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

//            Button(onClick = {
//                navController.navigate(Screen.Stores.withArgs("Juicy Grill"))
//            }) {
//                Text(text = "To Reviews")
//            }

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

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Επικεφαλίδα
                Text(
                    text = "Explore Our Restaurants",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                // "Available on" και Επιλογέας Ημέρας σε Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Text(
                        text = "Available on $selectedDay",
                        color = Color.Gray,
                        style = TextStyle(fontSize = 14.sp) ,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    DaySelector(
                        selectedDay = selectedDay,
                        onDaySelected = { day -> selectedDay = day },
                        modifier = Modifier
                            .width(120.dp) // Περιορισμός του μήκους σε 100dp
                    )
                }

                // Λίστα Εστιατορίων
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredStores) { store ->
                        RestaurantCard(
                            store = store,

                            onBookClick = {
                                navController.navigate(Screen.Stores.withArgs("Juicy Grill"))
                            },

                            onReviewClick = {
                                navController.navigate(Screen.Reviews.withArgs(store.storeId.toString()))
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
        shape = RoundedCornerShape(4.dp),
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
fun RestaurantCard(store: Store, onBookClick: () -> Unit, onReviewClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(4.dp),
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

            // Κουμπί "See Reviews"
            Button(
                onClick = onReviewClick,
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp, minWidth = 80.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    contentColor = Color.White
                )
            ) {
                Text(text = "See Reviews")
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

@Composable
fun DaySelector(selectedDay: String, onDaySelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val daysOfWeek = listOf("Mon-Sun", "Mon-Sat", "Mon-Fri", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 8.dp) // Προσαρμοσμένο padding για καλύτερη εμφάνιση
            .width(150.dp) // Περισσότερος χώρος για το DaySelector
            .drawBehind {
                // Υπογράμμιση
                drawLine(
                    color = Color(0xFFFFA726),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth() // Επιτρέπει καλύτερη διάταξη
        ) {
            Text(
                text = selectedDay,
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            daysOfWeek.forEach { day ->
                DropdownMenuItem(
                    text = { Text(day) },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}

