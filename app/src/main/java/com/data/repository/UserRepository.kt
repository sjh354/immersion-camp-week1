package com.data.repository

import com.data.remote.RetrofitClient
import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.FavoriteRequestDto
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.requestMenuListBySortingDto
import com.data.remote.dto.FavoriteResponseDto
import retrofit2.Call

class UserRepository {
    fun getCategoreis(): Call<CategoryListDto> {
        return RetrofitClient.api.getCategories()
    }

    fun getMenus(category: String): Call<MenuListDto> {
        return RetrofitClient.api.getMenus(category)
    }

    fun getMenusBySorting(category: String, reqBody: requestMenuListBySortingDto): Call<MenuListDto> {
        return RetrofitClient.api.getMenusBySorting(category, reqBody)
    }

    fun setIsFavorite(reqBody: FavoriteRequestDto): Call<FavoriteResponseDto> {
        return RetrofitClient.api.setIsFavorite(reqBody)
    }

}
