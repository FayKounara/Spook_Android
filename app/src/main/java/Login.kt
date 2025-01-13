package com.example.room_setup_composables

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import kotlinx.coroutines.delay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_setup_composables.ui.theme.Screen


@Composable
fun LoginNavigation(userViewModel:UserViewModel, storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, reviewViewModel: ReviewViewModel, slotViewModel: SlotViewModel) {

    val navController = rememberNavController()
    val users by userViewModel.allUsers.collectAsState(initial = emptyList())

    NavHost(navController = navController, startDestination = Screen.LoginPage.route) {
        composable(route = Screen.LoginPage.route) {
            val context = LocalContext.current
            LoginScreen(
                navController,
                onLoginClick = { username, password ->
                    // Check if user exists
                    val userId = users
                        .filter { it.username == username && it.password == password }
                        .map { it.userId }
                        .firstOrNull()

                    if (userId == null) {
                        Toast.makeText(context, "Incorrect Credentials, please try again", Toast.LENGTH_LONG).show()
                    } else {
                        navController.navigate(Screen.HomePage.withArgs(userId.toString()))
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.RegisterPage.withArgs())
                }
            )
        }

        // Navigation to RegisterPage, no need to dynamically pass parameters
        composable(
            route = Screen.RegisterPage.route,
        ) { entry ->
            RegisterNavigation(userViewModel, storeViewModel, bookingViewModel, reviewViewModel,slotViewModel)
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
        ) { entry ->
            val userId = entry.arguments?.getString("id") ?: "1"

            HomePageNavigation(userId = userId.toInt(), userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginClick: (username: String, password: String) -> Unit,
    onSignUpClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F4)) // Light background color
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Title
        Text(
            text = "Welcome Back!",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color =Color(0xFFFFA726)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username TextField
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username", color = Color(0xFF616161)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFFA726)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFA726),
                unfocusedBorderColor = Color(0xFFBDBDBD),
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFF616161)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFA726)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFA726),
                unfocusedBorderColor = Color(0xFFBDBDBD),
            )
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Login Button
        Button(
            onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    showError = true // Trigger error state
                } else {
                    onLoginClick(username, password)
                }
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
        ) {
            Text(text = "Login", color = Color.White, fontSize = 16.sp,fontWeight = FontWeight.Bold)
        }

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Username and password are required",
                color = Color(0xFFFD6924),
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(Unit) {
                delay(3000) // Delay for 2 seconds before hiding error message
                showError = false
            }
        }
        

        // Sign-Up Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don't have an Account?", color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onSignUpClick) {
                Text(text = "Sign Up", fontWeight = FontWeight.Bold,color = Color(0xFFFFA726))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Testing Button
        Button(
            onClick = {
                // Navigate to Profile screen with userId as argument
                navController.navigate(Screen.HomePage.withArgs("1"))
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))        ) {
            Text(text = "For testing: Go to HomePage", color = Color.White)
        }

        // Error Message

    }
}

