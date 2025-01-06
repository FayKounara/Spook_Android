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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_setup_composables.ui.theme.Screen


@Composable
fun AuthNavigation(userViewModel:UserViewModel ,storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, reviewViewModel: ReviewViewModel) {

    val navController = rememberNavController()
    val users by userViewModel.allUsers.collectAsState(initial = emptyList())

    NavHost(navController = navController, startDestination = Screen.AuthPage.route) {
        composable(route = Screen.AuthPage.route) {
            val context = LocalContext.current
            AuthScreen(
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
                    // Navigate to a sign-up screen
                    println("Sign Up clicked")
                }
            )
        }
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
            HomePageNavigation(userId = userId.toInt(), userViewModel, storeViewModel, bookingViewModel, reviewViewModel)
        }
    }
}

@Composable
fun AuthScreen(
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
            .padding(16.dp)
            .background(Color(0xFFFFFFFF)), // White background
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Login"
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username TextField
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    showError = true // Trigger error state
                } else {
                    onLoginClick(username, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign-Up Text
        Row {
            Text(text = "Don't have an Account?", color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onSignUpClick) {
                Text(text = "Sign Up", color = Color.Red)
            }
        }

        // For testing only
        Button(
            onClick = {
                // Navigate to Profile screen with userId as argument
                navController.navigate(Screen.HomePage.withArgs("1"))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "For testing: Go to HomePage", color = Color.White)
        }



        if (showError) {
            Text(text = "Username and password are required", color = Color.Red)
            LaunchedEffect(Unit) {
                delay(3000) // Delay for 2 seconds before hiding error message
                showError = false
            }
        }
    }
}

