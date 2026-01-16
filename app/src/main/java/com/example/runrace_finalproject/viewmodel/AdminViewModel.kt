package com.example.runrace_finalproject.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.Event
import com.example.runrace_finalproject.data.model.EventRequest
import com.example.runrace_finalproject.data.repository.EventRepository
import com.example.runrace_finalproject.data.repository.UploadRepository
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminEventState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val error: String? = null,
    val deleteSuccess: Boolean = false
)

data class AdminEventFormState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

data class UploadState(
    val isUploading: Boolean = false,
    val uploadedUrl: String? = null,
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {
    
    private val _listState = MutableStateFlow(AdminEventState())
    val listState: StateFlow<AdminEventState> = _listState.asStateFlow()
    
    private val _formState = MutableStateFlow(AdminEventFormState())
    val formState: StateFlow<AdminEventFormState> = _formState.asStateFlow()
    
    private val _uploadState = MutableStateFlow(UploadState())
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    
    init {
        loadAllEvents()
    }
    
    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState(isUploading = true)
            
            when (val result = uploadRepository.uploadImage(uri)) {
                is Resource.Success -> {
                    _uploadState.value = UploadState(
                        isUploading = false,
                        uploadedUrl = result.data
                    )
                }
                is Resource.Error -> {
                    _uploadState.value = UploadState(
                        isUploading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uploadState.value = UploadState(isUploading = true)
                }
            }
        }
    }
    
    fun resetUploadState() {
        _uploadState.value = UploadState()
    }
    
    fun loadAllEvents() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            
            when (val result = eventRepository.getAllEvents()) {
                is Resource.Success -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        events = result.data ?: emptyList(),
                        error = null
                    )
                }
                is Resource.Error -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _listState.value = _listState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun loadEventForEdit(eventId: Int) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true, error = null)
            
            when (val result = eventRepository.getEventById(eventId)) {
                is Resource.Success -> {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        event = result.data,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _formState.value = _formState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun createEvent(
        name: String,
        location: String,
        category: String,
        date: String,
        status: String,
        bannerUrl: String?
    ) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true, error = null)
            
            val request = EventRequest(name, location, category, date, status, bannerUrl)
            
            when (val result = eventRepository.createEvent(request)) {
                is Resource.Success -> {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        saveSuccess = true,
                        error = null
                    )
                    loadAllEvents()
                }
                is Resource.Error -> {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _formState.value = _formState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun updateEvent(
        eventId: Int,
        name: String,
        location: String,
        category: String,
        date: String,
        status: String,
        bannerUrl: String?
    ) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true, error = null)
            
            val request = EventRequest(name, location, category, date, status, bannerUrl)
            
            when (val result = eventRepository.updateEvent(eventId, request)) {
                is Resource.Success -> {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        saveSuccess = true,
                        error = null
                    )
                    loadAllEvents()
                }
                is Resource.Error -> {
                    _formState.value = _formState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _formState.value = _formState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            
            when (val result = eventRepository.deleteEvent(eventId)) {
                is Resource.Success -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        deleteSuccess = true,
                        error = null
                    )
                    loadAllEvents()
                }
                is Resource.Error -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _listState.value = _listState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun resetFormState() {
        _formState.value = AdminEventFormState()
    }
    
    fun resetDeleteSuccess() {
        _listState.value = _listState.value.copy(deleteSuccess = false)
    }
    
    fun clearError() {
        _listState.value = _listState.value.copy(error = null)
        _formState.value = _formState.value.copy(error = null)
    }
}
