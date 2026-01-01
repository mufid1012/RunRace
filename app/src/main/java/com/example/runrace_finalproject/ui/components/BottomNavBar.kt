package com.example.runrace_finalproject.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.runrace_finalproject.navigation.Screen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(Screen.Home.route, Icons.Default.Home, "Beranda")
    object Events : BottomNavItem(Screen.EventList.route, Icons.Default.Event, "Event")
    object MyEvents : BottomNavItem(Screen.MyEvents.route, Icons.Default.BookmarkAdded, "Event Saya")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Default.Person, "Profil")
}

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Events,
        BottomNavItem.MyEvents,
        BottomNavItem.Profile
    )
    
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) }
            )
        }
    }
}
