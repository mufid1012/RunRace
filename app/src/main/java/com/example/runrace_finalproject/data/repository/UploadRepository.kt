package com.example.runrace_finalproject.data.repository

import android.content.Context
import android.net.Uri
import com.example.runrace_finalproject.data.remote.UploadApi
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadRepository @Inject constructor(
    private val uploadApi: UploadApi,
    @ApplicationContext private val context: Context
) {
    
    suspend fun uploadImage(uri: Uri): Resource<String> {
        return try {
            // Get file from URI
            val file = getFileFromUri(uri) ?: return Resource.Error("Failed to read file")
            
            // Create multipart body
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
            
            // Upload
            val response = uploadApi.uploadImage(multipartBody)
            
            // Clean up temp file
            file.delete()
            
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data.url)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Upload failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "upload_${System.currentTimeMillis()}.jpg"
            val tempFile = File(context.cacheDir, fileName)
            
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
