package com.hjhan.moduletest.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.LaunchedEffect
import com.hjhan.moduletest.ui.detail.UserDetailScreen
import com.hjhan.moduletest.ui.detail.UserDetailViewModel
import com.hjhan.moduletest.ui.login.LoginScreen
import com.hjhan.moduletest.ui.main.MainScreen

private sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object Detail : Screen("detail/{userId}/{userName}") {
        fun createRoute(userId: Int, userName: String) =
            "detail/$userId/${Uri.encode(userName)}"
    }
}

@Composable
fun AppNavGraph(startLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (startLoggedIn) Screen.Main.route else Screen.Login.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                viewModel = hiltViewModel(),
                onNavigateToDetail = { userId, userName, _ ->
                    navController.navigate(Screen.Detail.createRoute(userId, userName))
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("userName") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: return@composable
            val userName = backStackEntry.arguments?.getString("userName")
            val viewModel: UserDetailViewModel = hiltViewModel()

            LaunchedEffect(userId) {
                viewModel.onIntent(UserDetailViewModel.Intent.LoadUser(userId))
            }

            UserDetailScreen(
                viewModel = viewModel,
                userName = userName,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
