package com.hjhan.moduletest.domain.usecase

import com.hjhan.moduletest.domain.repository.UserRepository
import com.hjhan.moduletest.domain.model.User
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<User> = userRepository.fetchUsers()
}
