package com.example.runrace_finalproject.data.repository

import com.example.runrace_finalproject.data.model.AuthResponse
import com.example.runrace_finalproject.data.model.LoginRequest
import com.example.runrace_finalproject.data.model.RegisterRequest
import com.example.runrace_finalproject.data.remote.AuthApi
import com.example.runrace_finalproject.utils.Resource
import com.example.runrace_finalproject.utils.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {
    
    suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.success && authResponse.token != null && authResponse.user != null) {
                    tokenManager.saveToken(authResponse.token)
                    tokenManager.saveUser(authResponse.user)
                    Resource.Success(authResponse)
                } else {
                    Resource.Error(authResponse.message)
                }
            } else {
                // Parse error body to get message
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val gson = com.google.gson.Gson()
                    val errorResponse = gson.fromJson(errorBody, AuthResponse::class.java)
                    errorResponse?.message ?: "Login gagal"
                } catch (e: Exception) {
                    "Login gagal"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Terjadi kesalahan")
        }
    }
    
    suspend fun register(name: String, email: String, password: String): Resource<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.success) {
                    Resource.Success(authResponse)
                } else {
                    Resource.Error(authResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun logout(): Resource<Unit> {
        return try {
            authApi.logout()
            tokenManager.clearAll()
            Resource.Success(Unit)
        } catch (e: Exception) {
            tokenManager.clearAll()
            Resource.Success(Unit)
        }
    }
    
    suspend fun getCurrentUser(): Resource<AuthResponse> {
        return try {
            val response = authApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                if (authResponse.success && authResponse.user != null) {
                    tokenManager.saveUser(authResponse.user)
                    Resource.Success(authResponse)
                } else {
                    Resource.Error(authResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get user")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
    
    fun isAdmin(): Boolean = tokenManager.isAdmin()
    
    fun getUserRole(): String? = tokenManager.getUserRole()
}
