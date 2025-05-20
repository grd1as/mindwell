package com.example.mindwell.app.data.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.mindwell.app.data.repositories.dataStore
import kotlinx.coroutines.flow.firstOrNull

object TokenStore {
    private val KEY = stringPreferencesKey("jwt")

    suspend fun save(token: String, ctx: Context) {
        ctx.dataStore.edit { it[KEY] = token }
    }
    suspend fun load(ctx: Context): String? =
        ctx.dataStore.data.firstOrNull()?.get(KEY)

    suspend fun clear(ctx: Context) {
        ctx.dataStore.edit { it.remove(KEY) }
    }
}
