package com.hjhan.moduletest.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjhan.moduletest.model.User
import com.hjhan.moduletest.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val user: User) : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class Intent {
        data class LoadUser(val userId: Int) : Intent()
        data class ToggleFavorite(val userId: Int, val isFavorite: Boolean) : Intent()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadUser -> loadUser(intent.userId)
            is Intent.ToggleFavorite -> toggleFavorite(intent.userId, intent.isFavorite)
        }
    }

    private fun loadUser(userId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { userRepository.getUserById(userId) }
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "오류가 발생했습니다") }
        }
    }

    private fun toggleFavorite(userId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userRepository.toggleFavorite(userId, isFavorite)
            val current = (_uiState.value as? UiState.Success)?.user ?: return@launch
            _uiState.value = UiState.Success(current.copy(isFavorite = isFavorite))
        }
    }
}
