package com.hjhan.moduletest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hjhan.moduletest.data.local.dao.UserRoomDao
import com.hjhan.moduletest.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserRoomDao
}
