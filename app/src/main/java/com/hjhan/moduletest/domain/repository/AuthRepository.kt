package com.hjhan.moduletest.domain.repository

interface AuthRepository {
    fun isLoggedIn(): Boolean
    fun getUsername(): String
    suspend fun login(username: String, token: String)
    fun logout()
}
