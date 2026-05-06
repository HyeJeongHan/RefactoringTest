package com.hjhan.moduletest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val website: String?,
    val addrStreet: String?,
    val addrSuite: String?,
    val addrCity: String?,
    val addrZipcode: String?,
    val companyName: String?,
    val companyCatchPhrase: String?,
    val lastUpdated: Long = 0L,
    val isFavorite: Boolean = false
)
