package com.example.runrace_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.Event
import com.example.runrace_finalproject.data.model.EventParticipant
import com.example.runrace_finalproject.data.repository.EventRepository
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventParticipantsState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val participants: List<EventParticipant> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class EventParticipantsViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(EventParticipantsState())
    val state: StateFlow<EventParticipantsState> = _state.asStateFlow()
    
    fun loadParticipants(eventId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = eventRepository.getEventParticipants(eventId)) {
                is Resource.Success -> {
                    result.data?.let { response ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            event = response.event,
                            participants = response.participants ?: emptyList(),
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
