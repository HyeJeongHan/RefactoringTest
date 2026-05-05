package com.hjhan.moduletest.network

import com.hjhan.moduletest.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("users")
    fun getUsers(): Call<List<User>>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>
}
