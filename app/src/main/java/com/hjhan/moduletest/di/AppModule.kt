package com.hjhan.moduletest.di

import android.content.Context
import com.hjhan.moduletest.database.DatabaseHelper
import com.hjhan.moduletest.database.UserDao
import com.hjhan.moduletest.network.ApiService
import com.hjhan.moduletest.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabaseHelper(@ApplicationContext context: Context): DatabaseHelper =
        DatabaseHelper.getInstance(context)

    @Provides
    @Singleton
    fun provideUserDao(dbHelper: DatabaseHelper): UserDao = UserDao(dbHelper)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
}
