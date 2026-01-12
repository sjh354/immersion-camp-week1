package com.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val name: String,
    val price: String,
    val img: String,
    val category: String,
    val store: String,
    val isFavorite: Boolean,
    val starpoint: Float,
    val deeplink: String
):Parcelable