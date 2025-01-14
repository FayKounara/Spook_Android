package com.example.room_setup_composables

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch
import com.example.room_setup_composables.ui.theme.Screen
import com.example.room_setup_composables.com.example.room_setup_composables.ui.theme.StoreNavigation
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.room_database_setup.R
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



@Composable
fun HomePageNavigation(
    userId: Int,
    userViewModel:UserViewModel,
    storeViewModel: StoreViewModel,
    bookingViewModel: BookingViewModel,
    reviewViewModel: ReviewViewModel,
    slotViewModel: SlotViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.HomePage.route) {
        // HomePage receive
        composable(route = Screen.HomePage.route) {
            Homepage(navController, userId, userViewModel = userViewModel, storeViewModel = storeViewModel,slotViewModel=slotViewModel)
            BottomNavBar(
                onHomeClick = {
                    navController.navigate(Screen.HomePage.route) {
                    popUpTo(Screen.HomePage.route) { inclusive = true } // Avoids stacking multiple Homepages
                } },
                onProfileClick = { navController.navigate(Screen.ProfileScreen.withArgs(userId.toString())) }
            )
        }

        composable(
            route = Screen.Stores.route + "/{name}/{selectedDay}/{selectedPersons}",
            arguments = listOf(

                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                },

                navArgument("selectedDay") {
                    type = NavType.StringType
                    nullable = true
                },

                navArgument("selectedPersons") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            val name = entry.arguments?.getString("name") ?: "Juicy Grill"
            val selectedDay = entry.arguments?.getString("selectedDay") ?: "Monday"
            val persons = entry.arguments?.getString("selectedPersons") ?: "2"
            StoreNavigation(userId, userViewModel, storeViewModel, bookingViewModel, reviewViewModel, name, selectedDay, persons.toInt(), slotViewModel)
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
            ReviewScreen(navController, userId, userViewModel, reviewViewModel, storeId = storeId)
        }

        // For navigation to profile
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


@SuppressLint("NewApi")
@Composable
fun Homepage(
    navController: NavController,
    userId: Int,
    userViewModel: UserViewModel,
    storeViewModel: StoreViewModel,
    slotViewModel: SlotViewModel,
    modifier: Modifier = Modifier
) {
    val stores by storeViewModel.allStores.collectAsState(initial = emptyList())
    val offers by storeViewModel.allOffers.collectAsState(initial = emptyList())
    val users by userViewModel.allUsers.collectAsState(initial = emptyList())
    val slots by slotViewModel.slots.collectAsState(initial = emptyList())

    fun getCurrentDayName(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dateFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
    }

    var selectedDay by remember {
        mutableStateOf(getCurrentDayName())
    }
    var selectedPersons by remember { mutableStateOf("2") }
    val coroutineScope = rememberCoroutineScope()
    val filteredStores = remember { mutableStateListOf<Store>() }

// LaunchedEffect to filter stores based on available slots
    LaunchedEffect(stores, selectedDay, selectedPersons) {
        filteredStores.clear()

        stores.forEach { store ->
            // Fetch the slots for each store directly using the new function
            val storeSlots = slotViewModel.fetchSlotsForStore(store.storeId)

            // Check if any slot for the store on the selected day has enough available slots
            val hasAvailableSlot = storeSlots.any { slot ->
                slot.day == selectedDay && slot.availability >= (selectedPersons.toIntOrNull() ?: 0)
            }

            // If a store has an available slot, add it to the filtered list
            if (hasAvailableSlot) {
                filteredStores.add(store)
            }
        }

        Log.d("Homepage", "Filtered Stores: ${filteredStores.size}")
    }

//fay

    val currentUser = users.firstOrNull { it.userId == userId }
    val username = currentUser?.username ?: "Guest"

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showErrorDialog) {
        Dialog(
            onDismissRequest = { showErrorDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .width(300.dp) // Μικρότερο πλάτος
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp), // Πιο στρογγυλεμένες γωνίες
                color = Color.White,
                shadowElevation = 10.dp // Προσθήκη σκιάς
            ) {
                Column(
                    modifier = Modifier.padding(16.dp) // Προσθήκη εσωτερικού περιθωρίου
                ) {
                    // Τίτλος
                    Text(
                        text = "Too Juicy to Handle!",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800) // Πορτοκαλί γραμματοσειρά
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // Κυρίως κείμενο
                    Text(
                        text = errorMessage,
                        style = TextStyle(
                            fontSize = 18.sp, // Μικρότερη γραμματοσειρά
                            color = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Κουμπί επιβεβαίωσης
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text(
                                "OK",
                                style = TextStyle(
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
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
                // Εικονίδιο Προφίλ
                Icon(
                    imageVector = Icons.Default.Person, // Χρησιμοποίησε ένα προκαθορισμένο εικονίδιο ή αντικατέστησέ το με το δικό σου
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(40.dp) // Μέγεθος του εικονιδίου
                        .clickable {
                            navController.navigate(Screen.ProfileScreen.withArgs(userId.toString())) // Ενέργεια που γίνεται όταν πατηθεί το εικονίδιο
                        }
                        .padding(8.dp) // Κενό γύρω από το εικονίδιο
                )

                Text(
                    text = "Welcome back, $username :)",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Today's Offers Section
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val isToday = selectedDay == getCurrentDayName()
                val offersTitle = if (isToday) "Today's Offers!" else "$selectedDay's Offers!"
                
                Text(
                    text = offersTitle,
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
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
                                // Έλεγχος αν το store της προσφοράς βρίσκεται στη λίστα filteredStores
                                val storeExistsInFilteredStores = filteredStores.any { store ->
                                    store.storeId == offer.storeId
                                }

                                if (storeExistsInFilteredStores) {
                                    coroutineScope.launch {
                                        val store = storeViewModel.getStoreById(offer.storeId)
                                        store?.let {
                                            navController.navigate(Screen.Stores.withArgs(it.name, selectedDay, selectedPersons))
                                        }
                                    }
                                } else {
                                    // Εμφάνιση μηνύματος σφάλματος
                                    errorMessage = "Wow, this deal’s on fire! \n\n We are fully booked for today, but check back tomorrow\n— we’d love to Juicy serve you;)"
                                    showErrorDialog = true
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Explore Restaurants Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Header
                Text(
                    text = "Explore Our Restaurants",
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                )

                // "Available on" και Επιλογέας Ημέρας
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Επιλογέας ατόμων
                    PersonsSelector(
                        selectedPersons = selectedPersons,
                        onPersonsSelected = { persons -> selectedPersons = persons },
                        modifier = Modifier.width(60.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Επιλογέας ημέρας
                    DaySelector(
                        selectedDay = selectedDay,
                        onDaySelected = { day -> selectedDay = day },
                        modifier = Modifier.width(120.dp)
                    )
                }

                // Λίστα Εστιατορίων
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                    //fay
                ) {
                  items(filteredStores) { store ->
                       RestaurantCard(
                          store = store,
                          onBookClick = {
                              navController.navigate(Screen.Stores.withArgs(store.name, selectedDay, selectedPersons))
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
    val foodImage = when {
        foodItem.name.contains("Pizza", ignoreCase = true) -> R.drawable.pizzaphoto
        foodItem.name.contains("Burger", ignoreCase = true) -> R.drawable.burgerphoto
        foodItem.name.contains("Pasta", ignoreCase = true) -> R.drawable.pastaphoto
        else -> R.drawable.burgerphoto // Προεπιλεγμένη εικόνα
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Καθορίζει το φόντο της κάρτας
        ),
        modifier = Modifier
            .width(200.dp)
            .padding(end = 16.dp)
            .clickable { onCardClick()}
    ) {
        Column {
            Image(
                painter = painterResource(id = foodImage),
              contentDescription = "Food Item",
               modifier = Modifier
                   .fillMaxWidth()
                   .height(120.dp),
              contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(8.dp), // Απόσταση γύρω από το κείμενο
                verticalArrangement = Arrangement.spacedBy(6.dp), // Απόσταση μεταξύ των στοιχείων
                horizontalAlignment = Alignment.Start // Ευθυγράμμιση στην αριστερή πλευρά
            ) {
                Text(
                    text = foodItem.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(1.dp)) // Κενό ανάμεσα στο όνομα και την τιμή/τοποθεσία
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Μικρότερη απόσταση μεταξύ τιμής και τοποθεσίας
                ) {
                    // Προβολή του ονόματος του μαγαζιού
                    Text(
                        text = "Book at $storeName",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = foodItem.orgPrice.toString(),//**************************
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Light,
                                color = Color.Gray,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough // Διαγράμμιση αρχικής τιμής
                            ),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = foodItem.discountPrice.toString(),//*****************
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFA726) // Πορτοκαλί χρώμα
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RestaurantCard(store: Store, onBookClick: () -> Unit) {
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
            // Δυναμική φόρτωση εικόνας βάσει του id του Store
            val imageRes = when (store.storeId) {
                1 -> R.drawable.rest1photo
                2 -> R.drawable.rest2photo
                3 -> R.drawable.rest3photo
                4 -> R.drawable.rest4photo
                else -> R.drawable.rest1photo // Εικόνα προεπιλογής
            }

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Restaurant Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Στήλη για Όνομα και Τοποθεσία
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = store.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = store.location,
                    fontSize = 18.sp,
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
                colors = ButtonDefaults.buttonColors(
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
    val daysOfWeek = listOf(
        "Monday", "Tuesday", "Wednesday",
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
                    strokeWidth = 3f
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
            .padding(horizontal = 4.dp, vertical = 4.dp) // Μικρότερο padding
            .width(80.dp) // Μειωμένο πλάτος
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

@Composable
fun BottomNavBar(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navItems = listOf(
        "Home" to Icons.Default.Home,
        "Profile" to Icons.Default.Person
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEach { (label, icon) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        when (label) {
                            "Home" -> onHomeClick()
                            "Profile" -> onProfileClick()
                        }
                    }
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "$label Icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = label)
                }
            }
        }
    }
}





