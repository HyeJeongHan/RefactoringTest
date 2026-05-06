package com.hjhan.moduletest.data.local.mapper

import com.hjhan.moduletest.data.local.entity.UserEntity
import com.hjhan.moduletest.domain.model.Address
import com.hjhan.moduletest.domain.model.Company
import com.hjhan.moduletest.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website,
    address = addrStreet?.let { Address(it, addrSuite ?: "", addrCity ?: "", addrZipcode ?: "") },
    company = companyName?.let { Company(it, companyCatchPhrase ?: "") },
    lastUpdated = lastUpdated,
    isFavorite = isFavorite
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website,
    addrStreet = address?.street,
    addrSuite = address?.suite,
    addrCity = address?.city,
    addrZipcode = address?.zipcode,
    companyName = company?.name,
    companyCatchPhrase = company?.catchPhrase,
    lastUpdated = lastUpdated,
    isFavorite = isFavorite
)
