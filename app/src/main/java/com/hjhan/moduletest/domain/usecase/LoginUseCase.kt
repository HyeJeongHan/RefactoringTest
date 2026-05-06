package com.hjhan.moduletest.domain.usecase

import com.hjhan.moduletest.domain.repository.AuthRepository
import com.hjhan.moduletest.util.Constants
import kotlinx.coroutines.delay
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Boolean {
        delay(1200)
        return if (username == Constants.TEST_USERNAME && password == Constants.TEST_PASSWORD) {
            authRepository.login(username, "token_${System.currentTimeMillis()}")
            true
        } else {
            false
        }
    }
}
