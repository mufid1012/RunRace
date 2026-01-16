package com.example.runrace_finalproject.data.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("url")
    val url: String,
    
    @SerializedName("filename")
    val filename: String
)
