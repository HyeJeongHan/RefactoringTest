package com.hjhan.moduletest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hjhan.moduletest.util.Constants
import com.hjhan.moduletest.util.SharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefsManager
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    sealed class Intent {
        data class Login(val username: String, val password: String) : Intent()
        object ResetState : Intent()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val isAlreadyLoggedIn: Boolean get() = sharedPrefs.isLoggedIn()

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.Login -> login(intent.username, intent.password)
            is Intent.ResetState -> _uiState.value = UiState.Idle
        }
    }

    private fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank() || password.length < 4) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1200)

            if (username == Constants.TEST_USERNAME && password == Constants.TEST_PASSWORD) {
                sharedPrefs.setLoggedIn(true)
                sharedPrefs.saveUsername(username)
                sharedPrefs.saveUserToken("token_${System.currentTimeMillis()}")
                _uiState.value = UiState.Success
            } else {
                _uiState.value = UiState.Error("아이디 또는 비밀번호가 올바르지 않습니다")
            }
        }
    }
}
