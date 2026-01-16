package com.example.runrace_finalproject.data.remote

import com.example.runrace_finalproject.data.model.ApiResponse
import com.example.runrace_finalproject.data.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {
    
    @Multipart
    @POST("uploads")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ApiResponse<UploadResponse>>
}
