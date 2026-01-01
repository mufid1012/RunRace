package com.example.runrace_finalproject.data.repository

import com.example.runrace_finalproject.data.model.News
import com.example.runrace_finalproject.data.model.NewsRequest
import com.example.runrace_finalproject.data.remote.NewsApi
import com.example.runrace_finalproject.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val newsApi: NewsApi
) {
    
    suspend fun getAllNews(): Resource<List<News>> {
        return try {
            val response = newsApi.getAllNews()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get news")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getFeaturedNews(): Resource<List<News>> {
        return try {
            val response = newsApi.getFeaturedNews()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get featured news")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getNewsById(id: Int): Resource<News> {
        return try {
            val response = newsApi.getNewsById(id)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get news")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createNews(request: NewsRequest): Resource<News> {
        return try {
            val response = newsApi.createNews(request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to create news")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateNews(id: Int, request: NewsRequest): Resource<News> {
        return try {
            val response = newsApi.updateNews(id, request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to update news")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun deleteNews(id: Int): Resource<Unit> {
        return try {
            val response = newsApi.deleteNews(id)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to delete news")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
