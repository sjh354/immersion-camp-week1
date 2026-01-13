package com.data.repository

import com.data.remote.RetrofitClient
import com.data.remote.dto.AuthGoogleReq
import com.data.remote.dto.AuthGoogleRes
import com.data.remote.dto.AuthStore
import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.FavoriteRequestDto
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.requestMenuListBySortingDto
import com.data.remote.dto.FavoriteResponseDto
import com.data.remote.dto.FavoritesRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class UserRepository(private val ctx: android.content.Context) {
    private fun bearer(): String =
        AuthStore.bearer(ctx) ?: throw IllegalStateException("No app_token. Login first.")

    fun authGoogle(reqBody: AuthGoogleReq): Call<AuthGoogleRes> {
        return RetrofitClient.api.authGoogle(reqBody)
    }
    fun getCategoreis(): Call<CategoryListDto> {
        return RetrofitClient.api.getCategories(bearer())
    }

    fun getMenus(category: String): Call<MenuListDto> {
        return RetrofitClient.api.getMenus(bearer(), category)
    }

    fun getMenusBySorting(category: String, reqBody: requestMenuListBySortingDto): Call<MenuListDto> {
        return RetrofitClient.api.getMenusBySorting(bearer(), category, reqBody)
    }

    fun setIsFavorite(reqBody: FavoriteRequestDto): Call<FavoriteResponseDto> {
        return RetrofitClient.api.setIsFavorite(bearer(), reqBody)
    }

    fun getFavorites(): Call<FavoritesRes> {
        return RetrofitClient.api.getFavorites(bearer())
    }
}
