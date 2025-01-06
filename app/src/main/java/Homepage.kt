package com.example.room_setup_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.example.room_setup_composables.ui.theme.Screen
import com.example.room_setup_composables.com.example.room_setup_composables.ui.theme.StoreNavigation


@Composable
fun HomePageNavigation(
    storeViewModel: StoreViewModel,
    bookingViewModel: BookingViewModel,
    reviewViewModel: ReviewViewModel
) {
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
                    defaultValue = "Juicy Grill"
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
fun Homepage(
    navController: NavController,
    name: String,
    storeViewModel: StoreViewModel,
    modifier: Modifier = Modifier
) {
    val stores by storeViewModel.allStores.collectAsState(initial = emptyList())
    val offers by storeViewModel.allOffers.collectAsState(initial = emptyList())
    var selectedDay by remember { mutableStateOf("Monday") }
    var selectedPersons by remember { mutableStateOf("2") }
    val coroutineScope = rememberCoroutineScope()

    // Φιλτράρισμα καταστημάτων με βάση την επιλεγμένη ημέρα
    val filteredStores = stores.filter { store ->
        val isDayMatch = store.avDays.split(",").contains(selectedDay)
        val isPersonsMatch = (store.availability >= (selectedPersons.toIntOrNull() ?: 0))
        (isDayMatch && isPersonsMatch)
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
                    text = "Welcome back, $name!",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Today's Offers Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Today's Offers",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(offers) { offer ->
                        var storeName by remember { mutableStateOf("Loading...") }

                        // Φόρτωσε το όνομα του μαγαζιού σε coroutine
                        LaunchedEffect(offer.storeId) {
                            val store = storeViewModel.getStoreById(offer.storeId)
                            storeName = store?.name ?: "Unknown Store"
                        }

                        FoodCard(
                            foodItem = offer,
                            storeName = storeName,
                            onCardClick = {
                                coroutineScope.launch {
                                    val store = storeViewModel.getStoreById(offer.storeId)
                                    store?.let {
                                        navController.navigate(Screen.Stores.withArgs(it.name))
                                    }
                                }
                            }
                        )
                    }
                }

            }

            // Explore Restaurants Section
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header
                Text(
                    text = "Explore Our Restaurants",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                )

                // "Available on" και Επιλογέας Ημέρας
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    /*Text(
                        text = "Available on $selectedDay for $selectedPersons persons",
                        color = Color.Gray,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(end = 12.dp)
                    )*/

                    PersonsSelector(
                        selectedPersons = selectedPersons,
                        onPersonsSelected = { persons -> selectedPersons = persons },
                        modifier = Modifier.width(100.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    DaySelector(
                        selectedDay = selectedDay,
                        onDaySelected = { day -> selectedDay = day },
                        modifier = Modifier.width(100.dp)
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
                                navController.navigate(Screen.Stores.withArgs(store.name))
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
fun FoodCard(foodItem: Offer, storeName: String, onCardClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(200.dp)
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = foodItem.name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            // Προβολή του ονόματος του μαγαζιού
            Text(
                text = "Find it at $storeName",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))

            Row {
                Text(
                    text = "${foodItem.orgPrice}",
                    textDecoration = TextDecoration.LineThrough,
                    color = Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                Text(text = "${foodItem.discountPrice}",fontWeight = FontWeight.Bold, color = Color(0xFFFFA726))
            }
        }
    }
}



@Composable
fun RestaurantCard(store: Store, onBookClick: () -> Unit, onReviewClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = store.name, fontWeight = FontWeight.Bold)
                Text(text = store.location, color = Color.Gray)
            }
            Button(onClick = onReviewClick,
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp, minWidth = 80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726), // Πορτοκαλί φόντο
                    contentColor = Color.White         // Λευκό κείμενο
                )
            )  { Text("See Reviews") }
            Button(onClick = onBookClick,
                modifier = Modifier
                    .defaultMinSize(minHeight = 40.dp, minWidth = 80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726), // Πορτοκαλί φόντο
                    contentColor = Color.White         // Λευκό κείμενο
                )
            )  { Text("Book") }
        }
    }
}


@Composable
fun DaySelector(selectedDay: String, onDaySelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val daysOfWeek = listOf(
        "Mon-Sun", "Mon-Sat", "Mon-Fri", "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday", "Saturday", "Sunday"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .width(150.dp)
            .drawBehind {
                drawLine(
                    color = Color(0xFFFFA726),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
            }
    ) {
        Text(
            text = selectedDay,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            daysOfWeek.forEach { day ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onDaySelected(day)
                    },
                    text = { Text(text = day) }
                )
            }
        }
    }
}

@Composable
fun PersonsSelector(selectedPersons: String, onPersonsSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val personOptions = listOf("2", "4", "6", "8")
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clickable { expanded = true }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .width(120.dp)
            .drawBehind {
                drawLine(
                    color = Color(0xFFFFA726),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2f
                )
            }
    ) {
        Text(
            text = "$selectedPersons Pax",
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            personOptions.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onPersonsSelected(option)
                    },
                    text = { Text(text = "$option Pax") }
                )
            }
        }
    }
}


