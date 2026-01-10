package com.data.remote

import com.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Call

interface ApiService {
    @GET("users/{id}")
    fun getUser(
        @Path("id") id: Int
    ): Call<UserDto>

    @GET("users")
    fun getUsers(
        @Query("limit") limit: Int
    ): Call<List<UserDto>>
}
