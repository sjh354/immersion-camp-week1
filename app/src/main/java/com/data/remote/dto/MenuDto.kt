package com.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val id: Int,
    val name: String,
    val price: String,
    val img: String,
    val category: String,
    val store: String,
    var isFavorite: Boolean,
    val starpoint: Float,
    val deeplink: String
):Parcelable