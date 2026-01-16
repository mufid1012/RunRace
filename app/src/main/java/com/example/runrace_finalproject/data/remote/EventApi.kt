package com.example.runrace_finalproject.data.remote

import com.example.runrace_finalproject.data.model.ApiResponse
import com.example.runrace_finalproject.data.model.Event
import com.example.runrace_finalproject.data.model.EventParticipantsResponse
import com.example.runrace_finalproject.data.model.EventRequest
import com.example.runrace_finalproject.data.model.Registration
import retrofit2.Response
import retrofit2.http.*

interface EventApi {
    
    @GET("events")
    suspend fun getAllEvents(): Response<ApiResponse<List<Event>>>
    
    @GET("events")
    suspend fun getEventsByStatus(@Query("status") status: String): Response<ApiResponse<List<Event>>>
    
    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): Response<ApiResponse<Event>>
    
    @POST("events")
    suspend fun createEvent(@Body request: EventRequest): Response<ApiResponse<Event>>
    
    @PUT("events/{id}")
    suspend fun updateEvent(
        @Path("id") id: Int,
        @Body request: EventRequest
    ): Response<ApiResponse<Event>>
    
    @DELETE("events/{id}")
    suspend fun deleteEvent(@Path("id") id: Int): Response<ApiResponse<Unit>>
    
    @POST("events/{id}/register")
    suspend fun registerForEvent(@Path("id") eventId: Int): Response<ApiResponse<Registration>>
    
    @DELETE("events/{id}/unregister")
    suspend fun unregisterFromEvent(@Path("id") eventId: Int): Response<ApiResponse<Unit>>
    
    @GET("registrations/my")
    suspend fun getMyRegistrations(): Response<ApiResponse<List<Registration>>>
    
    @GET("events/{id}/participants")
    suspend fun getEventParticipants(@Path("id") eventId: Int): Response<ApiResponse<EventParticipantsResponse>>
}
