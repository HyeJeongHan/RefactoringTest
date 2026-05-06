package com.hjhan.moduletest.data.repository

import com.hjhan.moduletest.data.local.preferences.AppPreferences
import com.hjhan.moduletest.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : AuthRepository {

    override fun isLoggedIn(): Boolean = appPreferences.isLoggedIn()

    override fun getUsername(): String = appPreferences.getUsername()

    override suspend fun login(username: String, token: String) {
        appPreferences.saveLoggedIn(true)
        appPreferences.saveUsername(username)
        appPreferences.saveUserToken(token)
    }

    override fun logout() = runBlocking { appPreferences.clearAuth() }
}
