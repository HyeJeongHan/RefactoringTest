package com.hjhan.moduletest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjhan.moduletest.model.User
import com.hjhan.moduletest.repository.UserRepository
import com.hjhan.moduletest.util.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPrefs: SharedPrefsManager
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

    val username: String get() = sharedPrefs.getUsername()
    val isLoggedIn: Boolean get() = sharedPrefs.isLoggedIn()

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
            runCatching { userRepository.fetchUsers() }
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
        userRepository.clearCache()
        loadUsers()
    }

    private fun search(query: String) {
        if (query.isBlank()) {
            _uiState.value = if (allUsers.isEmpty()) UiState.Empty else UiState.Success(allUsers)
            return
        }
        val filtered = allUsers.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true) ||
            it.phone?.contains(query, ignoreCase = true) == true
        }
        _uiState.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }

    private fun showFavorites() {
        viewModelScope.launch {
            val favorites = userRepository.getFavoriteUsers()
            _uiState.value = if (favorites.isEmpty()) UiState.Empty else UiState.Success(favorites)
        }
    }

    private fun toggleFavorite(userId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userRepository.toggleFavorite(userId, isFavorite)
        }
    }

    private fun logout() {
        sharedPrefs.clearAll()
    }
}
