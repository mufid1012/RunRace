package com.example.runrace_finalproject.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runrace_finalproject.data.model.User
import com.example.runrace_finalproject.data.repository.AuthRepository
import com.example.runrace_finalproject.data.repository.UploadRepository
import com.example.runrace_finalproject.data.repository.UserRepository
import com.example.runrace_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val updateSuccess: Boolean = false,
    val passwordChangeSuccess: Boolean = false
)

data class ProfileUploadState(
    val isUploading: Boolean = false,
    val uploadedUrl: String? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    private val _uploadState = MutableStateFlow(ProfileUploadState())
    val uploadState: StateFlow<ProfileUploadState> = _uploadState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = ProfileUploadState(isUploading = true)
            
            when (val result = uploadRepository.uploadImage(uri)) {
                is Resource.Success -> {
                    _uploadState.value = ProfileUploadState(
                        isUploading = false,
                        uploadedUrl = result.data
                    )
                }
                is Resource.Error -> {
                    _uploadState.value = ProfileUploadState(
                        isUploading = false,
                        error = result.message
                    )
                }
                is Resource.Loading -> {
                    _uploadState.value = ProfileUploadState(isUploading = true)
                }
            }
        }
    }
    
    fun resetUploadState() {
        _uploadState.value = ProfileUploadState()
    }
    
    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = userRepository.getProfile()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = result.data,
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
    
    fun updateProfile(name: String, photoUrl: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = userRepository.updateProfile(name, photoUrl)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = result.data,
                        updateSuccess = true,
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
    
    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            when (val result = userRepository.changePassword(currentPassword, newPassword)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        passwordChangeSuccess = true,
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
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
    
    fun resetUpdateSuccess() {
        _state.value = _state.value.copy(updateSuccess = false)
    }
    
    fun resetPasswordChangeSuccess() {
        _state.value = _state.value.copy(passwordChangeSuccess = false)
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
