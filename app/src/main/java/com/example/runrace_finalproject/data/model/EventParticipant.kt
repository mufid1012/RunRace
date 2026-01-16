package com.example.runrace_finalproject.data.model

import com.google.gson.annotations.SerializedName

data class EventParticipant(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("user_id")
    val userId: Int,
    
    @SerializedName("user_name")
    val userName: String,
    
    @SerializedName("user_email")
    val userEmail: String,
    
    @SerializedName("user_photo_url")
    val userPhotoUrl: String? = null,
    
    @SerializedName("registered_at")
    val registeredAt: String
)

data class EventParticipantsResponse(
    @SerializedName("event")
    val event: Event? = null,
    
    @SerializedName("participants")
    val participants: List<EventParticipant>? = null
)
