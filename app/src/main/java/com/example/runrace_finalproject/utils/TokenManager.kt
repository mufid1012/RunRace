package com.example.runrace_finalproject.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.runrace_finalproject.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    }
    
    fun getToken(): String? {
        return prefs.getString(Constants.KEY_TOKEN, null)
    }
    
    fun clearToken() {
        prefs.edit().remove(Constants.KEY_TOKEN).apply()
    }
    
    fun saveUser(user: User) {
        prefs.edit().apply {
            putInt(Constants.KEY_USER_ID, user.id)
            putString(Constants.KEY_USER_ROLE, user.role)
            putString(Constants.KEY_USER_NAME, user.name)
            putString(Constants.KEY_USER_EMAIL, user.email)
            apply()
        }
    }
    
    fun getUserId(): Int {
        return prefs.getInt(Constants.KEY_USER_ID, -1)
    }
    
    fun getUserRole(): String? {
        return prefs.getString(Constants.KEY_USER_ROLE, null)
    }
    
    fun getUserName(): String? {
        return prefs.getString(Constants.KEY_USER_NAME, null)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(Constants.KEY_USER_EMAIL, null)
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    
    fun isAdmin(): Boolean {
        return getUserRole() == Constants.ROLE_ADMIN
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
