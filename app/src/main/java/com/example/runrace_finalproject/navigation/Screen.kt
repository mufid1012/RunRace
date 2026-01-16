package com.example.runrace_finalproject.navigation

sealed class Screen(val route: String) {
    // Auth
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    
    // User screens
    object Home : Screen("home")
    object EventList : Screen("event_list")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: Int) = "event_detail/$eventId"
    }
    object MyEvents : Screen("my_events")
    object NewsDetail : Screen("news_detail/{newsId}") {
        fun createRoute(newsId: Int) = "news_detail/$newsId"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object ChangePassword : Screen("change_password")
    
    // Admin screens
    object AdminDashboard : Screen("admin_dashboard")
    object AdminEvents : Screen("admin_events")
    object AddEvent : Screen("add_event")
    object EditEvent : Screen("edit_event/{eventId}") {
        fun createRoute(eventId: Int) = "edit_event/$eventId"
    }
    object AdminNews : Screen("admin_news")
    object AddNews : Screen("add_news")
    object EditNews : Screen("edit_news/{newsId}") {
        fun createRoute(newsId: Int) = "edit_news/$newsId"
    }
    object EventParticipants : Screen("event_participants/{eventId}") {
        fun createRoute(eventId: Int) = "event_participants/$eventId"
    }
}
