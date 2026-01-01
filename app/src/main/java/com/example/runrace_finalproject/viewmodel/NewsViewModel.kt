package com.example.runrace_finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.News
import com.example.runrace_finalproject.data.repository.NewsRepository
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsDetailState(
    val isLoading: Boolean = false,
    val news: News? = null,
    val error: String? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {
    
    private val _detailState = MutableStateFlow(NewsDetailState())
    val detailState: StateFlow<NewsDetailState> = _detailState.asStateFlow()
    
    fun loadNewsById(id: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true, error = null)
            
            when (val result = newsRepository.getNewsById(id)) {
                is Resource.Success -> {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        news = result.data,
                        error = null
                    )
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
}
