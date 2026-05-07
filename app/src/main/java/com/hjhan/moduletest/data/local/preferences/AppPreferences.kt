package com.hjhan.moduletest.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, "ModuleTestPrefs"))
    }
)

@Singleton
class AppPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
        val USER_TOKEN = stringPreferencesKey("user_token")
        val LAST_FETCH_TIME = longPreferencesKey("last_fetch_time")
    }

    // 동기 읽기 — 인증 상태는 비suspend 컨텍스트에서 접근하므로 runBlocking 사용
    fun isLoggedIn(): Boolean = runBlocking {
        context.dataStore.data.map { it[Keys.IS_LOGGED_IN] ?: false }.first()
    }

    fun getUsername(): String = runBlocking {
        context.dataStore.data.map { it[Keys.USERNAME] ?: "" }.first()
    }

    suspend fun getLastFetchTime(): Long =
        context.dataStore.data.map { it[Keys.LAST_FETCH_TIME] ?: 0L }.first()

    suspend fun saveLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[Keys.IS_LOGGED_IN] = loggedIn }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { it[Keys.USERNAME] = username }
    }

    suspend fun saveUserToken(token: String) {
        context.dataStore.edit { it[Keys.USER_TOKEN] = token }
    }

    suspend fun saveLastFetchTime(time: Long) {
        context.dataStore.edit { it[Keys.LAST_FETCH_TIME] = time }
    }

    suspend fun clearAuth() {
        context.dataStore.edit {
            it.remove(Keys.IS_LOGGED_IN)
            it.remove(Keys.USERNAME)
            it.remove(Keys.USER_TOKEN)
        }
    }
}
