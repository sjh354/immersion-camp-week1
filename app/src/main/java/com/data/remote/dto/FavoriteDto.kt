package com.data.remote.dto

data class FavoriteRequestDto(
    val id: Int,
    val changeto: Boolean
)

data class FavoriteResponseDto(
    val success: Boolean,
    val id: Int,
    val isFavorite: Boolean
)

data class FavoritesRes(val favorites: List<Int>)

