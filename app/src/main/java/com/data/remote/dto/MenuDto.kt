package com.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val name: String,
    val price: String,
    val img: String,
    val category: String,
    val store: String,
    val isFavorite: Boolean
):Parcelable