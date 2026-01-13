package com.data.remote

import com.data.remote.dto.AuthGoogleReq
import com.data.remote.dto.AuthGoogleRes
import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.FavoriteRequestDto
import com.data.remote.dto.FavoriteResponseDto
import com.data.remote.dto.FavoritesRes
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.requestMenuListBySortingDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/google")
    fun authGoogle(@Body body: AuthGoogleReq): Call<AuthGoogleRes>

    @GET("api/categories")
    fun getCategories(
        @Header("Authorization") authorization: String,
        ): Call<CategoryListDto>

    @GET("api/menus/{category}")
    fun getMenus(
        @Header("Authorization") authorization: String,
        @Path("category") category: String
    ): Call<MenuListDto>

    @POST("api/menus/{category}")
    fun getMenusBySorting(
        @Header("Authorization") authorization: String,
        @Path("category") category: String,
        @Body body: requestMenuListBySortingDto
    ): Call<MenuListDto>

    @POST("api/menus/favorite")
    fun setIsFavorite(
        @Header("Authorization") authorization: String,
        @Body body: FavoriteRequestDto
    ): Call<FavoriteResponseDto>

    @GET("api/menus/favorites")
    fun getFavorites(
        @Header("Authorization") authorization: String
    ): Call<FavoritesRes>

}
