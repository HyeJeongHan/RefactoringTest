package com.hjhan.moduletest.data.repository

import com.hjhan.moduletest.domain.repository.AuthRepository
import com.hjhan.moduletest.util.SharedPrefsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val sharedPrefs: SharedPrefsManager
) : AuthRepository {

    override fun isLoggedIn(): Boolean = sharedPrefs.isLoggedIn()

    override fun getUsername(): String = sharedPrefs.getUsername()

    override fun login(username: String, token: String) {
        sharedPrefs.setLoggedIn(true)
        sharedPrefs.saveUsername(username)
        sharedPrefs.saveUserToken(token)
    }

    override fun logout() = sharedPrefs.clearAll()
}
