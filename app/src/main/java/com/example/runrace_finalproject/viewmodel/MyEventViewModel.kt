package com.example.runrace_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.Registration
import com.example.runrace_finalproject.data.repository.EventRepository
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyEventState(
    val isLoading: Boolean = false,
    val registrations: List<Registration> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MyEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(MyEventState())
    val state: StateFlow<MyEventState> = _state.asStateFlow()
    
    init {
        loadMyRegistrations()
    }
    
    fun loadMyRegistrations() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = eventRepository.getMyRegistrations()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        registrations = result.data ?: emptyList(),
                        error = null
                    )
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
    
    fun refresh() {
        loadMyRegistrations()
    }
}
