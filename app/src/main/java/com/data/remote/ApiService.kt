package com.data.remote

import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.FavoriteRequestDto
import com.data.remote.dto.FavoriteResponseDto
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.requestMenuListBySortingDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @GET("api/categories")
    fun getCategories(): Call<CategoryListDto>

    @GET("api/menus/{category}")
    fun getMenus(
        @Path("category") category: String
    ): Call<MenuListDto>

    @POST("api/menus/{category}")
    fun getMenusBySorting(
        @Path("category") category: String,
        @Body body: requestMenuListBySortingDto
    ): Call<MenuListDto>

    @POST("api/menus/favorite")
    fun setIsFavorite(
        @Body body: FavoriteRequestDto
    ): Call<FavoriteResponseDto>
}
