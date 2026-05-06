package com.hjhan.moduletest.data.repository

import com.hjhan.moduletest.data.local.dao.UserRoomDao
import com.hjhan.moduletest.data.local.mapper.toDomain
import com.hjhan.moduletest.data.local.mapper.toEntity
import com.hjhan.moduletest.data.local.preferences.AppPreferences
import com.hjhan.moduletest.data.remote.mapper.toDomain
import com.hjhan.moduletest.domain.model.User
import com.hjhan.moduletest.domain.repository.UserRepository
import com.hjhan.moduletest.network.ApiService
import com.hjhan.moduletest.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserRoomDao,
    private val apiService: ApiService,
    private val appPreferences: AppPreferences
) : UserRepository {

    override suspend fun fetchUsers(): List<User> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val lastFetch = appPreferences.getLastFetchTime()

        if (now - lastFetch < Constants.DEFAULT_CACHE_DURATION_MS) {
            val cached = userDao.getAllUsers().map { it.toDomain() }
            if (cached.isNotEmpty()) return@withContext cached
        }

        try {
            val users = apiService.getUsers().map { it.toDomain(lastUpdated = now) }
            userDao.insertUsers(users.map { it.toEntity() })
            appPreferences.saveLastFetchTime(now)
            users
        } catch (e: Exception) {
            userDao.getAllUsers().map { it.toDomain() }.takeIf { it.isNotEmpty() } ?: throw e
        }
    }

    override suspend fun getUserById(id: Int): User = withContext(Dispatchers.IO) {
        userDao.getUserById(id)?.toDomain() ?: run {
            val now = System.currentTimeMillis()
            val user = apiService.getUserById(id).toDomain(lastUpdated = now)
            userDao.insertUsers(listOf(user.toEntity()))
            user
        }
    }

    override suspend fun toggleFavorite(userId: Int, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        userDao.updateFavorite(userId, isFavorite)
    }

    override suspend fun getFavoriteUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getFavoriteUsers().map { it.toDomain() }
    }

    override fun clearCache() = runBlocking { appPreferences.saveLastFetchTime(0L) }
}
