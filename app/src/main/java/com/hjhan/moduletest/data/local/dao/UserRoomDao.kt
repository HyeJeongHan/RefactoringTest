package com.hjhan.moduletest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hjhan.moduletest.data.local.entity.UserEntity

@Dao
interface UserRoomDao {

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("UPDATE users SET isFavorite = :isFavorite WHERE id = :userId")
    suspend fun updateFavorite(userId: Int, isFavorite: Boolean)

    @Query("SELECT * FROM users WHERE isFavorite = 1")
    suspend fun getFavoriteUsers(): List<UserEntity>
}
