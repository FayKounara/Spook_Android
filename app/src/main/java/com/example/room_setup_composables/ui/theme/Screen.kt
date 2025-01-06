package com.example.room_setup_composables.ui.theme

sealed class Screen(val route: String) {
    data object AuthPage : Screen("AuthScreen")
    data object HomePage : Screen("Homepage")
    data object ProfileScreen : Screen("ProfileScreen")
    data object Stores : Screen("StoreList")
    data object Bookings : Screen("BookingsScreen")
    data object Reviews : Screen("ReviewScreen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg->
                append("/$arg")
            }
        }
    }
}