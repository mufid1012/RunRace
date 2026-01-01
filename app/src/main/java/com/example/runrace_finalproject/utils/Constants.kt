package com.example.runrace_finalproject.utils

object Constants {
    // TODO: Update this with your actual backend API URL
    // For Android Emulator: use 10.0.2.2 to access localhost
    // For Physical Device: use your computer's IP address
    const val BASE_URL = "http://10.0.2.2/backend/api/"
    
    // SharedPreferences keys
    const val PREFS_NAME = "runrace_prefs"
    const val KEY_TOKEN = "jwt_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    
    // Event status
    const val STATUS_ONGOING = "ongoing"
    const val STATUS_UPCOMING = "upcoming"
    const val STATUS_COMPLETED = "completed"
    
    // User roles
    const val ROLE_USER = "user"
    const val ROLE_ADMIN = "admin"
}
