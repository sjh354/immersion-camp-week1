package com.data.remote.dto

object AuthStore {
    private const val PREF = "auth"
    private const val KEY = "app_token"

    fun getToken(ctx: android.content.Context): String? =
        ctx.getSharedPreferences(PREF, android.content.Context.MODE_PRIVATE)
            .getString(KEY, null)

    fun setToken(ctx: android.content.Context, token: String) {
        ctx.getSharedPreferences(PREF, android.content.Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, token)
            .apply()
    }

    fun bearer(ctx: android.content.Context): String? =
        getToken(ctx)?.let { "Bearer $it" }
}
