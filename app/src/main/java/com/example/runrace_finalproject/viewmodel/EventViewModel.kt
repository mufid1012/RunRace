package com.example.runrace_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.Event
import com.example.runrace_finalproject.data.repository.EventRepository
import com.example.runrace_finalproject.utils.Constants
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventListState(
    val isLoading: Boolean = false,
    val allEvents: List<Event> = emptyList(),
    val ongoingEvents: List<Event> = emptyList(),
    val completedEvents: List<Event> = emptyList(),
    val selectedTabIndex: Int = 0,
    val error: String? = null
)

data class EventDetailState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val isRegistering: Boolean = false,
    val isUnregistering: Boolean = false,
    val isRegistered: Boolean = false,
    val registrationSuccess: Boolean = false,
    val unregistrationSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    
    private val _listState = MutableStateFlow(EventListState())
    val listState: StateFlow<EventListState> = _listState.asStateFlow()
    
    private val _detailState = MutableStateFlow(EventDetailState())
    val detailState: StateFlow<EventDetailState> = _detailState.asStateFlow()
    
    init {
        loadAllEvents()
    }
    
    fun loadAllEvents() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            
            when (val result = eventRepository.getAllEvents()) {
                is Resource.Success -> {
                    val events = result.data ?: emptyList()
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        allEvents = events,
                        ongoingEvents = events.filter { it.status == Constants.STATUS_ONGOING },
                        completedEvents = events.filter { it.status == Constants.STATUS_COMPLETED },
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
    
    fun selectTab(index: Int) {
        _listState.value = _listState.value.copy(selectedTabIndex = index)
    }
    
    fun loadEventDetail(eventId: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true, error = null)
            
            when (val result = eventRepository.getEventById(eventId)) {
                is Resource.Success -> {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        event = result.data,
                        error = null
                    )
                    // Check if user is registered for this event
                    checkRegistrationStatus(eventId)
                }
                is Resource.Error -> {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _detailState.value = _detailState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    private fun checkRegistrationStatus(eventId: Int) {
        viewModelScope.launch {
            when (val result = eventRepository.getMyRegistrations()) {
                is Resource.Success -> {
                    val isRegistered = result.data?.any { it.eventId == eventId } ?: false
                    _detailState.value = _detailState.value.copy(isRegistered = isRegistered)
                }
                else -> {}
            }
        }
    }
    
    fun registerForEvent(eventId: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isRegistering = true, error = null)
            
            when (val result = eventRepository.registerForEvent(eventId)) {
                is Resource.Success -> {
                    _detailState.value = _detailState.value.copy(
                        isRegistering = false,
                        isRegistered = true,
                        registrationSuccess = true,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _detailState.value = _detailState.value.copy(
                        isRegistering = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _detailState.value = _detailState.value.copy(isRegistering = true)
                }
            }
        }
    }
    
    fun unregisterFromEvent(eventId: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isUnregistering = true, error = null)
            
            when (val result = eventRepository.unregisterFromEvent(eventId)) {
                is Resource.Success -> {
                    _detailState.value = _detailState.value.copy(
                        isUnregistering = false,
                        isRegistered = false,
                        unregistrationSuccess = true,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _detailState.value = _detailState.value.copy(
                        isUnregistering = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _detailState.value = _detailState.value.copy(isUnregistering = true)
                }
            }
        }
    }
    
    fun resetRegistrationSuccess() {
        _detailState.value = _detailState.value.copy(registrationSuccess = false)
    }
    
    fun resetUnregistrationSuccess() {
        _detailState.value = _detailState.value.copy(unregistrationSuccess = false)
    }
    
    fun clearDetailError() {
        _detailState.value = _detailState.value.copy(error = null)
    }
}
