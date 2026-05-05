package com.hjhan.moduletest.repository

import android.util.Log
import com.hjhan.moduletest.database.UserDao
import com.hjhan.moduletest.model.User
import com.hjhan.moduletest.network.ApiService
import com.hjhan.moduletest.util.Constants
import com.hjhan.moduletest.util.SharedPrefsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val sharedPrefs: SharedPrefsManager
) {

    companion object {
        private const val TAG = "UserRepository"
    }

    interface UserListCallback {
        fun onSuccess(users: List<User>)
        fun onError(error: String)
    }

    interface SingleUserCallback {
        fun onSuccess(user: User)
        fun onError(error: String)
    }

    fun fetchUsers(callback: UserListCallback) {
        val lastFetch = sharedPrefs.getLastFetchTime()
        val now = System.currentTimeMillis()

        if (now - lastFetch < Constants.DEFAULT_CACHE_DURATION_MS) {
            val cached = userDao.getAllUsers()
            if (cached.isNotEmpty()) {
                Log.d(TAG, "Returning ${cached.size} users from cache")
                callback.onSuccess(cached)
                return
            }
        }

        apiService.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    users.forEach { it.lastUpdated = now }

                    Thread {
                        userDao.saveUsers(users)
                        sharedPrefs.saveLastFetchTime(now)
                        Log.d(TAG, "Saved ${users.size} users to DB")
                    }.start()

                    callback.onSuccess(users)
                } else {
                    Log.w(TAG, "API error: ${response.code()}")
                    callback.onError("서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e(TAG, "Network failure", t)
                val fallback = userDao.getAllUsers()
                if (fallback.isNotEmpty()) {
                    Log.d(TAG, "Fallback to DB: ${fallback.size} users")
                    callback.onSuccess(fallback)
                } else {
                    callback.onError(t.message ?: "네트워크 오류")
                }
            }
        })
    }

    fun getUserById(id: Int, callback: SingleUserCallback) {
        val cached = userDao.getUserById(id)
        if (cached != null) {
            callback.onSuccess(cached)
            return
        }

        apiService.getUserById(id).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        user.lastUpdated = System.currentTimeMillis()
                        Thread { userDao.saveUser(user) }.start()
                        callback.onSuccess(user)
                    } else {
                        callback.onError("사용자를 찾을 수 없습니다")
                    }
                } else {
                    callback.onError("서버 오류: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                callback.onError(t.message ?: "네트워크 오류")
            }
        })
    }

    fun toggleFavorite(userId: Int, isFavorite: Boolean) {
        userDao.updateFavorite(userId, isFavorite)
    }

    fun getFavoriteUsers(): List<User> = userDao.getFavoriteUsers()

    fun searchUsers(query: String): List<User> {
        return userDao.getAllUsers().filter {
            it.name.contains(query, ignoreCase = true) ||
            it.email.contains(query, ignoreCase = true) ||
            (it.phone?.contains(query, ignoreCase = true) == true)
        }
    }

    fun clearCache() {
        sharedPrefs.saveLastFetchTime(0L)
    }
}
