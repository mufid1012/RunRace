package com.example.runrace_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.Event
import com.example.runrace_finalproject.data.model.News
import com.example.runrace_finalproject.data.repository.EventRepository
import com.example.runrace_finalproject.data.repository.NewsRepository
import com.example.runrace_finalproject.utils.Constants
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val isLoading: Boolean = false,
    val featuredEvents: List<Event> = emptyList(),
    val news: List<News> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            // Load featured events (ongoing + upcoming)
            val eventsResult = eventRepository.getAllEvents()
            val newsResult = newsRepository.getAllNews()
            
            val featuredEvents = when (eventsResult) {
                is Resource.Success -> eventsResult.data?.filter { 
                    it.status == Constants.STATUS_ONGOING || it.status == Constants.STATUS_UPCOMING 
                }?.take(5) ?: emptyList()
                else -> emptyList()
            }
            
            val newsList = when (newsResult) {
                is Resource.Success -> newsResult.data ?: emptyList()
                else -> emptyList()
            }
            
            val error = when {
                eventsResult is Resource.Error -> eventsResult.message
                newsResult is Resource.Error -> newsResult.message
                else -> null
            }
            
            _state.value = _state.value.copy(
                isLoading = false,
                featuredEvents = featuredEvents,
                news = newsList,
                error = error
            )
        }
    }
    
    fun refresh() {
        loadHomeData()
    }
}
