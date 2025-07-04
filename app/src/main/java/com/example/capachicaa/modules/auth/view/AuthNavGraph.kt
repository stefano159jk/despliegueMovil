package com.example.capachicaa.modules.auth.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.capachicaa.modules.auth.controller.AuthController
import com.example.capachicaa.modules.auth.view.LoginScreen
import com.example.capachicaa.modules.auth.view.RegisterScreen

@Composable
fun AuthNavGraph(
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val controller = AuthController(navController.context)

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                controller = controller,
                navController = navController,
                onNavigateToRegister = { navController.navigate("register") },
                onAuthSuccess = onAuthSuccess
            )
        }

        composable("register") {
            RegisterScreen(
                controller = controller,
                onSuccess = onAuthSuccess,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
    }
}
