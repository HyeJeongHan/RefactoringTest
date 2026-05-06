package com.hjhan.moduletest.data.remote.mapper

import com.hjhan.moduletest.data.remote.model.UserDto
import com.hjhan.moduletest.domain.model.Address
import com.hjhan.moduletest.domain.model.Company
import com.hjhan.moduletest.domain.model.User

fun UserDto.toDomain(lastUpdated: Long = 0L): User = User(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website,
    address = address?.let { Address(it.street, it.suite, it.city, it.zipcode) },
    company = company?.let { Company(it.name, it.catchPhrase) },
    lastUpdated = lastUpdated
)
