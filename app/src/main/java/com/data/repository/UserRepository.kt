package com.data.repository

import com.data.remote.RetrofitClient
import com.data.remote.dto.UserDto
import retrofit2.Call

class UserRepository {

    fun getUser(id: Int): Call<UserDto> {
        return RetrofitClient.api.getUser(id)
    }

    fun getUsers(limit: Int): Call<List<UserDto>> {
        return RetrofitClient.api.getUsers(limit)
    }
}
