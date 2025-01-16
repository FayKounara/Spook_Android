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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import kotlinx.coroutines.delay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.room_setup_composables.BookingViewModel
import com.example.room_setup_composables.HomePageNavigation
import com.example.room_setup_composables.LoginNavigation
import com.example.room_setup_composables.ReviewViewModel
import com.example.room_setup_composables.SlotViewModel
import com.example.room_setup_composables.StoreViewModel
import com.example.room_setup_composables.User
import com.example.room_setup_composables.UserViewModel
import com.example.room_setup_composables.ui.theme.Screen
import java.util.Random

@Composable
fun RegisterNavigation(userViewModel: UserViewModel, storeViewModel: StoreViewModel, bookingViewModel: BookingViewModel, reviewViewModel: ReviewViewModel, slotViewModel: SlotViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.RegisterPage.route) {

        composable(route = Screen.RegisterPage.route) {
            RegisterScreen(
                navController,
                onLoginClick = {
                    navController.navigate(Screen.LoginPage.withArgs())
                },
                onRegisterClick = { username, password, phoneNumber, email ->
                    val newUserId = Random().nextInt(999999999)
                    userViewModel.insertUser(
                        User(
                            userId = newUserId,
                            username = username,
                            password = password,
                            phoneNumber = phoneNumber,
                            email = email
                        )
                    )
                    navController.navigate(Screen.HomePage.withArgs(newUserId.toString()))
                }
            )
        }

        // Navigation to LoginPage, no need to dynamically pass parameters
        composable(
            route = Screen.LoginPage.route,
        ) { _ ->
            LoginNavigation(userViewModel, storeViewModel, bookingViewModel, reviewViewModel, slotViewModel)
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
            HomePageNavigation(userId = userId.toInt(), userViewModel, storeViewModel, bookingViewModel, reviewViewModel,slotViewModel)
        }
    }
}

@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterClick: (username: String, password: String, phoneNumber: String, email: String) -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var usernameUnsafeInputs by remember { mutableStateOf(false) }
    var phoneUnsafeInputs by remember { mutableStateOf(false) }
    var emailUnsafeInputs by remember { mutableStateOf(false) }
    var passwordUnsafeInputs by remember { mutableStateOf(false) }
    var unsafeInputs by remember { mutableStateOf(false) }


    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordSafe by remember { mutableStateOf(true) }
    var isUsernameValid by remember { mutableStateOf(true) }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var errorInfo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F4))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Title
        Text(
            text = "Welcome!",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color =Color(0xFFFFA726)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username TextField
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                isUsernameValid = it.length > 3 },
            label = { Text("Username", color = Color(0xFF616161)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFFA726)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFA726),
                unfocusedBorderColor = Color(0xFFBDBDBD),
            )
        )

        if (usernameUnsafeInputs) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Username must be at least 4 characters",
                color = Color(0xFFFD6924),
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(Unit) {
                delay(3000) // Delay for 3 seconds before hiding error message
                usernameUnsafeInputs = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // PhoneNumber TextField
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                isPhoneNumberValid = it.length == 10 },
            label = { Text("Phone Number", color = Color(0xFF616161)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFFFA726)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFA726),
                unfocusedBorderColor = Color(0xFFBDBDBD),
            )
        )

        if (phoneUnsafeInputs) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Invalid Phone Number",
                color = Color(0xFFFD6924),
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(Unit) {
                delay(3000) // Delay for 3 seconds before hiding error message
                phoneUnsafeInputs = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = it.matches(Regex(emailPattern)) },
            label = { Text("Email", color = Color(0xFF616161)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFFFA726)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFA726),
                unfocusedBorderColor = Color(0xFFBDBDBD),
            )
        )

        if (emailUnsafeInputs) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Invalid email format",
                color = Color(0xFFFD6924),
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(Unit) {
                delay(3000)
                emailUnsafeInputs = false
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordSafe = it.length >= 8 &&
                        it.any { char -> char.isUpperCase() } &&
                        it.any { char -> char.isLowerCase() } &&
                        it.any { char -> char.isDigit() } &&
                        it.any { char -> "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~".contains(char) } &&
                        !it.contains(" ")
            },
            label = { Text("Password", color = Color(0xFF616161)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFA726)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFFA726),
                unfocusedBorderColor = Color(0xFFBDBDBD),
            )
        )

        if (passwordUnsafeInputs) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Password must be 8+ characters, include upper/lowercase letters, a digit, a special character, and no spaces",
                color = Color(0xFFFD6924),
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(Unit) {
                delay(3000)
                passwordUnsafeInputs = false
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Register Button
        Button(
            onClick = {
                // Many if statements so that users can see their errors one by one and avoid overcrowding of info
                if (!isUsernameValid || !isPhoneNumberValid || !isEmailValid || !isPasswordSafe || username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    if (!isUsernameValid) {
                        usernameUnsafeInputs = true
                    }
                    if (!isPhoneNumberValid) {
                        phoneUnsafeInputs = true
                    }
                    if (!isEmailValid) {
                        emailUnsafeInputs = true
                    }
                    if (!isPasswordSafe) {
                        passwordUnsafeInputs = true
                    }
                    if (username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        unsafeInputs = true // Trigger error state
                        errorInfo = "All fields are required!"
                    }
                } else {
                    onRegisterClick(username, password, phoneNumber, email)
                }
            },
            modifier = Modifier
                .width(200.dp)
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
        ) {
            Text(text = "Register", color = Color.White, fontSize = 16.sp,fontWeight = FontWeight.Bold)
        }

        // Capable to display whatever error message about any field that the user hasn't filled out properly
        if (unsafeInputs) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorInfo,
                color = Color(0xFFFD6924),
                fontWeight = FontWeight.Bold,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            LaunchedEffect(Unit) {
                delay(3000)
                unsafeInputs = false
            }
        }

        // Sign-Up Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already Registered?", color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onLoginClick) {
                Text(text = "Log In", fontWeight = FontWeight.Bold,color = Color(0xFFFFA726))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

