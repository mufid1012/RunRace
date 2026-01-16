package com.example.runrace_finalproject.data.repository

import com.example.runrace_finalproject.data.model.Event
import com.example.runrace_finalproject.data.model.EventParticipantsResponse
import com.example.runrace_finalproject.data.model.EventRequest
import com.example.runrace_finalproject.data.model.Registration
import com.example.runrace_finalproject.data.remote.EventApi
import com.example.runrace_finalproject.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventApi: EventApi
) {
    
    suspend fun getAllEvents(): Resource<List<Event>> {
        return try {
            val response = eventApi.getAllEvents()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get events")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getEventsByStatus(status: String): Resource<List<Event>> {
        return try {
            val response = eventApi.getEventsByStatus(status)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get events")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getEventById(id: Int): Resource<Event> {
        return try {
            val response = eventApi.getEventById(id)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get event")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createEvent(request: EventRequest): Resource<Event> {
        return try {
            val response = eventApi.createEvent(request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to create event")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateEvent(id: Int, request: EventRequest): Resource<Event> {
        return try {
            val response = eventApi.updateEvent(id, request)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to update event")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun deleteEvent(id: Int): Resource<Unit> {
        return try {
            val response = eventApi.deleteEvent(id)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to delete event")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun registerForEvent(eventId: Int): Resource<Registration> {
        return try {
            val response = eventApi.registerForEvent(eventId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to register for event")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getMyRegistrations(): Resource<List<Registration>> {
        return try {
            val response = eventApi.getMyRegistrations()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get registrations")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun unregisterFromEvent(eventId: Int): Resource<Unit> {
        return try {
            val response = eventApi.unregisterFromEvent(eventId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to unregister from event")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getEventParticipants(eventId: Int): Resource<EventParticipantsResponse> {
        return try {
            val response = eventApi.getEventParticipants(eventId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data)
                } else {
                    Resource.Error(apiResponse.message)
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get participants")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
