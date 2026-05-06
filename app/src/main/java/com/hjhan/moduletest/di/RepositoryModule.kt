package com.hjhan.moduletest.di

import com.hjhan.moduletest.data.repository.AuthRepositoryImpl
import com.hjhan.moduletest.data.repository.UserRepositoryImpl
import com.hjhan.moduletest.domain.repository.AuthRepository
import com.hjhan.moduletest.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
