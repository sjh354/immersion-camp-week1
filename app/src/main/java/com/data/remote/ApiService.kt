package com.data.remote

import com.data.remote.dto.UserDto
import com.data.remote.dto.CategoryListDto
import com.data.remote.dto.MenuListDto
import com.data.remote.dto.requestMenuListByGpsDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // 위에꺼 두개는 그냥 API 테스트용으로 하등 쓸데없는거임
    @GET("users/{id}")
    fun getUser(
        @Path("id") id: Int
    ): Call<UserDto>
    @GET("users")
    fun getUsers(
        @Query("limit") limit: Int
    ): Call<List<UserDto>>



    @GET("api/categories")
    fun getCategories(): Call<CategoryListDto>

    @GET("api/menus/{category}")
    fun getMenus(
        @Path("category") category: String
    ): Call<MenuListDto>

    @POST("api/menus/{category}")
    fun getMenusByGPS(
        @Path("category") category: String,
        @Body body: requestMenuListByGpsDto
    ): Call<MenuListDto>
}
