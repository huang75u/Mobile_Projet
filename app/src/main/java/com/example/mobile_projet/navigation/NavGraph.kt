package com.example.mobile_projet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_projet.screens.ExerciseScreen
import com.example.mobile_projet.screens.HomeScreen
import com.example.mobile_projet.screens.PointsRulesScreen
import com.example.mobile_projet.screens.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(route = BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(route = BottomNavItem.Exercise.route) {
            ExerciseScreen()
        }
        composable(route = BottomNavItem.Profile.route) {
            ProfileScreen()
        }
        composable(route = "points_rules") {
            PointsRulesScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

