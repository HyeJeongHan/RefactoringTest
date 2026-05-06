package com.hjhan.moduletest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjhan.moduletest.domain.repository.AuthRepository
import com.hjhan.moduletest.domain.usecase.GetFavoriteUsersUseCase
import com.hjhan.moduletest.domain.usecase.GetUsersUseCase
import com.hjhan.moduletest.domain.usecase.LogoutUseCase
import com.hjhan.moduletest.domain.usecase.RefreshUsersUseCase
import com.hjhan.moduletest.domain.usecase.SearchUsersUseCase
import com.hjhan.moduletest.domain.usecase.ToggleFavoriteUseCase
import com.hjhan.moduletest.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getFavoriteUsersUseCase: GetFavoriteUsersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val users: List<User>) : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class Intent {
        object LoadUsers : Intent()
        object Refresh : Intent()
        data class Search(val query: String) : Intent()
        object ShowFavorites : Intent()
        data class ToggleFavorite(val userId: Int, val isFavorite: Boolean) : Intent()
        object Logout : Intent()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var allUsers: List<User> = emptyList()

    val username: String get() = authRepository.getUsername()
    val isLoggedIn: Boolean get() = authRepository.isLoggedIn()

    init {
        onIntent(Intent.LoadUsers)
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadUsers -> loadUsers()
            is Intent.Refresh -> refresh()
            is Intent.Search -> search(intent.query)
            is Intent.ShowFavorites -> showFavorites()
            is Intent.ToggleFavorite -> toggleFavorite(intent.userId, intent.isFavorite)
            is Intent.Logout -> logout()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { getUsersUseCase() }
                .onSuccess { users ->
                    allUsers = users
                    _uiState.value = if (users.isEmpty()) UiState.Empty else UiState.Success(users)
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "네트워크 오류")
                }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            runCatching { refreshUsersUseCase() }
                .onSuccess { users ->
                    allUsers = users
                    _uiState.value = if (users.isEmpty()) UiState.Empty else UiState.Success(users)
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "네트워크 오류")
                }
        }
    }

    private fun search(query: String) {
        val results = searchUsersUseCase(allUsers, query)
        _uiState.value = if (results.isEmpty()) UiState.Empty else UiState.Success(results)
    }

    private fun showFavorites() {
        viewModelScope.launch {
            val favorites = getFavoriteUsersUseCase()
            _uiState.value = if (favorites.isEmpty()) UiState.Empty else UiState.Success(favorites)
        }
    }

    private fun toggleFavorite(userId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(userId, isFavorite)
            allUsers = allUsers.map { user ->
                if (user.id == userId) user.copy(isFavorite = isFavorite) else user
            }
            val current = _uiState.value
            if (current is UiState.Success) {
                _uiState.value = UiState.Success(
                    current.users.map { user ->
                        if (user.id == userId) user.copy(isFavorite = isFavorite) else user
                    }
                )
            }
        }
    }

    private fun logout() = logoutUseCase()
}
