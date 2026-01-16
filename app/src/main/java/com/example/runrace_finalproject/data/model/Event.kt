package com.example.runrace_finalproject.data.model

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nama_event")
    val name: String,
    
    @SerializedName("lokasi")
    val location: String,
    
    @SerializedName("kategori")
    val category: String,
    
    @SerializedName("tanggal")
    val date: String,
    
    @SerializedName("status")
    val status: String, // "ongoing", "upcoming", "completed"
    
    @SerializedName("banner_url")
    val bannerUrl: String? = null,
    
    @SerializedName("registration_count")
    val registrationCount: Int = 0
)
