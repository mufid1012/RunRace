package com.example.runrace_finalproject.data.repository

import com.example.runrace_finalproject.data.model.ChangePasswordRequest
import com.example.runrace_finalproject.data.model.UpdateProfileRequest
import com.example.runrace_finalproject.data.model.User
import com.example.runrace_finalproject.data.remote.UserApi
import com.example.runrace_finalproject.utils.Resource
import com.example.runrace_finalproject.utils.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val tokenManager: TokenManager
) {
    
    suspend fun getProfile(): Resource<User> {
        return try {
            val response = userApi.getProfile()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    tokenManager.saveUser(apiResponse.data)
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateProfile(name: String, photoUrl: String?): Resource<User> {
        return try {
            val response = userApi.updateProfile(UpdateProfileRequest(name, photoUrl))
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    tokenManager.saveUser(apiResponse.data)
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to update profile")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun changePassword(currentPassword: String, newPassword: String): Resource<Unit> {
        return try {
            val response = userApi.changePassword(ChangePasswordRequest(currentPassword, newPassword))
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to change password")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    fun getCachedUserName(): String? = tokenManager.getUserName()
    
    fun getCachedUserEmail(): String? = tokenManager.getUserEmail()
}
