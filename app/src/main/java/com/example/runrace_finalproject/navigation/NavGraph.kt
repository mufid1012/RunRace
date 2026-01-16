package com.example.runrace_finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.runrace_finalproject.ui.admin.AddEditEventScreen
import com.example.runrace_finalproject.ui.admin.AdminDashboardScreen
import com.example.runrace_finalproject.ui.admin.AdminEventScreen
import com.example.runrace_finalproject.ui.admin.EventParticipantsScreen
import com.example.runrace_finalproject.ui.auth.LoginScreen
import com.example.runrace_finalproject.ui.auth.SplashScreen
import com.example.runrace_finalproject.ui.event.EventDetailScreen
import com.example.runrace_finalproject.ui.event.EventListScreen
import com.example.runrace_finalproject.ui.home.HomeScreen
import com.example.runrace_finalproject.ui.myevent.MyEventScreen
import com.example.runrace_finalproject.ui.profile.ChangePasswordScreen
import com.example.runrace_finalproject.ui.profile.EditProfileScreen
import com.example.runrace_finalproject.ui.profile.ProfileScreen

import androidx.compose.ui.Modifier

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth Screens
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            com.example.runrace_finalproject.ui.auth.RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // User Screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onNavigateToNewsDetail = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }
        
        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(navArgument("newsId") { type = NavType.IntType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getInt("newsId") ?: return@composable
            com.example.runrace_finalproject.ui.news.NewsDetailScreen(
                newsId = newsId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.EventList.route) {
            EventListScreen(
                onNavigateToDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                }
            )
        }
        
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.MyEvents.route) {
            MyEventScreen(
                onNavigateToDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEdit = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToChangePassword = { navController.navigate(Screen.ChangePassword.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Admin Screens
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToEvents = { navController.navigate(Screen.AdminEvents.route) },
                onNavigateToNews = { navController.navigate(Screen.AdminNews.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.AdminEvents.route) {
            AdminEventScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Screen.AddEvent.route) },
                onNavigateToEdit = { eventId ->
                    navController.navigate(Screen.EditEvent.createRoute(eventId))
                },
                onNavigateToParticipants = { eventId ->
                    navController.navigate(Screen.EventParticipants.createRoute(eventId))
                }
            )
        }
        
        composable(Screen.AddEvent.route) {
            AddEditEventScreen(
                eventId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditEvent.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
            AddEditEventScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EventParticipants.route,
            arguments = listOf(navArgument("eventId") { type = NavType.IntType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId") ?: return@composable
            EventParticipantsScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AdminNews.route) {
            com.example.runrace_finalproject.ui.admin.AdminNewsScreen(
                onNavigateBack = { navController.popBackStack() },
                onAddNews = { navController.navigate(Screen.AddNews.route) },
                onEditNews = { newsId ->
                    navController.navigate(Screen.EditNews.createRoute(newsId))
                }
            )
        }
        
        composable(Screen.AddNews.route) {
            com.example.runrace_finalproject.ui.admin.AddEditNewsScreen(
                newsId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditNews.route,
            arguments = listOf(navArgument("newsId") { type = NavType.IntType })
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getInt("newsId") ?: return@composable
            com.example.runrace_finalproject.ui.admin.AddEditNewsScreen(
                newsId = newsId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
