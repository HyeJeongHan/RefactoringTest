package com.hjhan.moduletest.domain.usecase

import com.hjhan.moduletest.domain.repository.AuthRepository
import javax.inject.Inject

class GetUsernameUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String = authRepository.getUsername()
}
