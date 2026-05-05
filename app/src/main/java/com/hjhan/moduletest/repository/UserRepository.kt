package com.hjhan.moduletest.repository

import com.hjhan.moduletest.database.UserDao
import com.hjhan.moduletest.model.User
import com.hjhan.moduletest.network.ApiService
import com.hjhan.moduletest.util.Constants
import com.hjhan.moduletest.util.SharedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    suspend fun fetchUsers(): List<User> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val lastFetch = sharedPrefs.getLastFetchTime()

        if (now - lastFetch < Constants.DEFAULT_CACHE_DURATION_MS) {
            val cached = userDao.getAllUsers()
            if (cached.isNotEmpty()) return@withContext cached
        }

        try {
            val users = apiService.getUsers()
            users.forEach { it.lastUpdated = now }
            userDao.saveUsers(users)
            sharedPrefs.saveLastFetchTime(now)
            users
        } catch (e: Exception) {
            userDao.getAllUsers().takeIf { it.isNotEmpty() } ?: throw e
        }
    }

    suspend fun getUserById(id: Int): User = withContext(Dispatchers.IO) {
        userDao.getUserById(id) ?: run {
            val user = apiService.getUserById(id)
            user.lastUpdated = System.currentTimeMillis()
            userDao.saveUser(user)
            user
        }
    }

    suspend fun toggleFavorite(userId: Int, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        userDao.updateFavorite(userId, isFavorite)
    }

    suspend fun getFavoriteUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getFavoriteUsers()
    }

    fun clearCache() {
        sharedPrefs.saveLastFetchTime(0L)
    }
}
