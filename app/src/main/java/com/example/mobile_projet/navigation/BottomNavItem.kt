package com.example.mobile_projet.navigation

import androidx.annotation.DrawableRes
import com.example.mobile_projet.R

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    val title: String
) {
    object Home : BottomNavItem(
        route = "home",
        icon = R.drawable.ic_home,
        title = "Global"
    )
    
    object Exercise : BottomNavItem(
        route = "exercise",
        icon = R.drawable.ic_exercise,
        title = "Exercise"
    )
    
    object Profile : BottomNavItem(
        route = "profile",
        icon = R.drawable.ic_profile,
        title = "Profile"
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Exercise,
    BottomNavItem.Profile
)

