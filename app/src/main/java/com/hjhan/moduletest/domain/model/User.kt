package com.hjhan.moduletest.domain.model

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String?,
    val website: String?,
    val address: Address?,
    val company: Company?,
    val lastUpdated: Long = 0L,
    val isFavorite: Boolean = false
)

data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String
)

data class Company(
    val name: String,
    val catchPhrase: String
)
