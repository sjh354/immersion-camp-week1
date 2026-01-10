package com.data.repository

import com.data.remote.RetrofitClient
import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.Menu
import com.data.remote.dto.UserDto
import retrofit2.Call

class UserRepository {

    fun getUser(id: Int): Call<UserDto> {
        return RetrofitClient.api.getUser(id)
    }

    fun getUsers(limit: Int): Call<List<UserDto>> {
        return RetrofitClient.api.getUsers(limit)
    }

    fun getCategoreis(): Call<CategoryListDto> {
        return RetrofitClient.api.getCategories()
    }

    fun getMenus(category: String): Call<List<Menu>> {
        return RetrofitClient.api.getMenus(category)
    }

}
