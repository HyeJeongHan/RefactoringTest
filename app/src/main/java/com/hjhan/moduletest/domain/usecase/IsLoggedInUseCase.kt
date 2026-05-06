package com.hjhan.moduletest.domain.usecase

import com.hjhan.moduletest.domain.repository.AuthRepository
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean = authRepository.isLoggedIn()
}
