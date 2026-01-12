package com.data.repository

import com.data.remote.RetrofitClient
import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.UserDto
import com.data.remote.dto.requestMenuListBySortingDto
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

    fun getMenus(category: String): Call<MenuListDto> {
        return RetrofitClient.api.getMenus(category)
    }

    fun getMenusBySorting(category: String, reqBody: requestMenuListBySortingDto): Call<MenuListDto> {
        return RetrofitClient.api.getMenusBySorting(category, reqBody)
    }

}
