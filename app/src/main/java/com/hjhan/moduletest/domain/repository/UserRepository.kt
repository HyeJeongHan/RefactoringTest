package com.hjhan.moduletest.domain.repository

import com.hjhan.moduletest.model.User

interface UserRepository {
    suspend fun fetchUsers(): List<User>
    suspend fun getUserById(id: Int): User
    suspend fun toggleFavorite(userId: Int, isFavorite: Boolean)
    suspend fun getFavoriteUsers(): List<User>
    fun clearCache()
}
