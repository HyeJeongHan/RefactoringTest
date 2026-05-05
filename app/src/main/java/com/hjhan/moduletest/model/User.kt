package com.hjhan.moduletest.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val website: String?,
    val address: Address?,
    val company: Company?,
    var lastUpdated: Long = 0L,
    var isFavorite: Boolean = false
)

data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String
)

data class Company(
    val name: String,
    @SerializedName("catchPhrase") val catchPhrase: String
)