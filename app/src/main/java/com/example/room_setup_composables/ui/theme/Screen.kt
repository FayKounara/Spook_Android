package com.example.room_setup_composables

sealed class Screen(val route: String) {
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