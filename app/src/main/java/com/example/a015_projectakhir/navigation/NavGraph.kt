package com.example.a015_projectakhir.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a015_projectakhir.data.network.RetrofitClient
import com.example.a015_projectakhir.screen.AdminScreen
import com.example.a015_projectakhir.screen.LoginScreen
import com.example.a015_projectakhir.screen.StaffScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val apiService = RetrofitClient.instance

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                apiService = apiService,
                onNavigateToAdmin = {
                    navController.navigate("admin") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToStaff = {
                    navController.navigate("staff") { popUpTo("login") { inclusive = true } }
                }
            )
        }

        composable("admin") {
            AdminScreen(
                apiService = apiService,
                onLogout = {
                    navController.navigate("login") { popUpTo("admin") { inclusive = true } }
                }
            )
        }

        composable("staff") {
            StaffScreen(
                apiService = apiService,
                onLogout = {
                    navController.navigate("login") { popUpTo("staff") { inclusive = true } }
                }
            )
        }
    }
}