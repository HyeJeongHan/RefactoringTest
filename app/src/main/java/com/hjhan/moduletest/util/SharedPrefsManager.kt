package com.hjhan.moduletest.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsManager @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        private const val PREFS_NAME = "ModuleTestPrefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_LAST_FETCH_TIME = "last_fetch_time"
        private const val KEY_USER_TOKEN = "user_token"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    fun setLoggedIn(loggedIn: Boolean) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply()

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun saveUsername(username: String) = prefs.edit().putString(KEY_USERNAME, username).apply()

    fun getLastFetchTime(): Long = prefs.getLong(KEY_LAST_FETCH_TIME, 0L)
    fun saveLastFetchTime(time: Long) = prefs.edit().putLong(KEY_LAST_FETCH_TIME, time).apply()

    fun getUserToken(): String = prefs.getString(KEY_USER_TOKEN, "") ?: ""
    fun saveUserToken(token: String) = prefs.edit().putString(KEY_USER_TOKEN, token).apply()

    fun clearAll() = prefs.edit().clear().apply()
}
