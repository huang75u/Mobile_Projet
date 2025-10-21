package com.example.mobile_projet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile_projet.screens.ExerciseScreen
import com.example.mobile_projet.screens.FriendDetailsScreen
import com.example.mobile_projet.screens.FriendsScreen
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
            ExerciseScreen(navController = navController)
        }
        composable(route = BottomNavItem.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(route = "points_rules") {
            PointsRulesScreen(onBackClick = { navController.popBackStack() })
        }
        composable(route = "friends") {
            FriendsScreen(
                onBack = { navController.popBackStack() },
                onShowAll = { friendUid -> navController.navigate("friend_details/" + friendUid) }
            )
        }
        composable(route = "friend_details/{friendUid}") { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("friendUid") ?: ""
            FriendDetailsScreen(friendUid = uid, onBack = { navController.popBackStack() })
        }
    }
}

