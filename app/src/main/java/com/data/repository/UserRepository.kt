package com.data.repository

import com.data.remote.RetrofitClient
import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.UserDto
import com.data.remote.dto.requestMenuListByGpsDto
import com.ui.gpsInfo
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

    fun getMenusByGPS(category: String, gps: requestMenuListByGpsDto): Call<MenuListDto> {
        return RetrofitClient.api.getMenusByGPS(category, gps)
    }

}
