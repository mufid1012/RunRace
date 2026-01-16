package com.example.runrace_finalproject.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.News
import com.example.runrace_finalproject.data.model.NewsRequest
import com.example.runrace_finalproject.data.repository.NewsRepository
import com.example.runrace_finalproject.data.repository.UploadRepository
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminNewsState(
    val isLoading: Boolean = false,
    val news: List<News> = emptyList(),
    val error: String? = null,
    val deleteSuccess: Boolean = false
)

data class AddEditNewsState(
    val isLoading: Boolean = false,
    val news: News? = null,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

data class NewsUploadState(
    val isUploading: Boolean = false,
    val uploadedUrl: String? = null,
    val error: String? = null
)

@HiltViewModel
class AdminNewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {
    
    private val _listState = MutableStateFlow(AdminNewsState())
    val listState: StateFlow<AdminNewsState> = _listState.asStateFlow()
    
    private val _editState = MutableStateFlow(AddEditNewsState())
    val editState: StateFlow<AddEditNewsState> = _editState.asStateFlow()
    
    private val _uploadState = MutableStateFlow(NewsUploadState())
    val uploadState: StateFlow<NewsUploadState> = _uploadState.asStateFlow()
    
    init {
        loadAllNews()
    }
    
    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = NewsUploadState(isUploading = true)
            
            when (val result = uploadRepository.uploadImage(uri)) {
                is Resource.Success -> {
                    _uploadState.value = NewsUploadState(
                        isUploading = false,
                        uploadedUrl = result.data
                    )
                }
                is Resource.Error -> {
                    _uploadState.value = NewsUploadState(
                        isUploading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uploadState.value = NewsUploadState(isUploading = true)
                }
            }
        }
    }
    
    fun resetUploadState() {
        _uploadState.value = NewsUploadState()
    }
    
    fun loadAllNews() {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            
            when (val result = newsRepository.getAllNews()) {
                is Resource.Success -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        news = result.data ?: emptyList(),
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
    
    fun loadNewsById(id: Int) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isLoading = true, error = null)
            
            when (val result = newsRepository.getNewsById(id)) {
                is Resource.Success -> {
                    _editState.value = _editState.value.copy(
                        isLoading = false,
                        news = result.data,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _editState.value = _editState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _editState.value = _editState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun createNews(title: String, content: String, imageUrl: String?) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isLoading = true, error = null)
            
            val request = NewsRequest(title, content, imageUrl?.takeIf { it.isNotBlank() })
            
            when (val result = newsRepository.createNews(request)) {
                is Resource.Success -> {
                    _editState.value = _editState.value.copy(
                        isLoading = false,
                        saveSuccess = true,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _editState.value = _editState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _editState.value = _editState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun updateNews(id: Int, title: String, content: String, imageUrl: String?) {
        viewModelScope.launch {
            _editState.value = _editState.value.copy(isLoading = true, error = null)
            
            val request = NewsRequest(title, content, imageUrl?.takeIf { it.isNotBlank() })
            
            when (val result = newsRepository.updateNews(id, request)) {
                is Resource.Success -> {
                    _editState.value = _editState.value.copy(
                        isLoading = false,
                        saveSuccess = true,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _editState.value = _editState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _editState.value = _editState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun deleteNews(id: Int) {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            
            when (val result = newsRepository.deleteNews(id)) {
                is Resource.Success -> {
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        deleteSuccess = true,
                        error = null
                    )
                    loadAllNews()
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
    
    fun resetDeleteSuccess() {
        _listState.value = _listState.value.copy(deleteSuccess = false)
    }
    
    fun resetSaveSuccess() {
        _editState.value = _editState.value.copy(saveSuccess = false)
    }
    
    fun clearEditError() {
        _editState.value = _editState.value.copy(error = null)
    }
    
    fun resetEditState() {
        _editState.value = AddEditNewsState()
    }
}
