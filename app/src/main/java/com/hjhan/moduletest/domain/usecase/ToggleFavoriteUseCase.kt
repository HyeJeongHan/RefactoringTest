package com.hjhan.moduletest.domain.usecase

import com.hjhan.moduletest.domain.repository.UserRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Int, isFavorite: Boolean) =
        userRepository.toggleFavorite(userId, isFavorite)
}
