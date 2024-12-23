package com.example.room_setup_composables

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val users by viewModel.users.collectAsState()
    val stores by viewModel.stores.collectAsState()
    val bookings by viewModel.bookings.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var storeName by remember { mutableStateOf("") }
    var storeInfo by remember { mutableStateOf("") }

    var bookingDate by remember { mutableStateOf("") }
    var bookingHours by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Database CRUD Operations") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // User Input
                Text("Insert User")
                BasicTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(Modifier.padding(4.dp)) { innerTextField() }
                    }
                )
                BasicTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(Modifier.padding(4.dp)) { innerTextField() }
                    }
                )
                Button(
                    onClick = { viewModel.insertUser(User(username = username, password = password)) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Insert User")
                }

                // Store Input
                Text("Insert Store")
                BasicTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(Modifier.padding(4.dp)) { innerTextField() }
                    }
                )
                BasicTextField(
                    value = storeInfo,
                    onValueChange = { storeInfo = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(Modifier.padding(4.dp)) { innerTextField() }
                    }
                )
                Button(
                    onClick = { viewModel.insertStore(Store(name = storeName, info = storeInfo, avDays = "", avHours = "", location = "")) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Insert Store")
                }

                // Booking Input
                Text("Insert Booking")
                BasicTextField(
                    value = bookingDate,
                    onValueChange = { bookingDate = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(Modifier.padding(4.dp)) { innerTextField() }
                    }
                )
                BasicTextField(
                    value = bookingHours,
                    onValueChange = { bookingHours = it },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    decorationBox = { innerTextField ->
                        Box(Modifier.padding(4.dp)) { innerTextField() }
                    }
                )
                Button(
                    onClick = { viewModel.insertBooking(Booking(date = bookingDate, hours = bookingHours, storeId = 0, userId = 0)) },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Insert Booking")
                }

                // Display Data
                Spacer(modifier = Modifier.height(16.dp))
                Text("Users:")
                users.forEach { Text(it.username) }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Stores:")
                stores.forEach { Text(it.name) }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Bookings:")
                bookings.forEach { Text(it.date) }
            }
        }
    )


}

