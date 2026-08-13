package com.example.habitudes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class PreferencesManager(private val context: Context) {

    private val gson = Gson()

    companion object {
        val HABITS_KEY = stringPreferencesKey("habits_list")
        val VIE_KEY = stringPreferencesKey("stat_vie")
        val EXP_KEY = stringPreferencesKey("stat_exp")
        val MP_KEY = stringPreferencesKey("stat_mp")
        val PROFILE_IMAGE_KEY = stringPreferencesKey("profile_image_uri")
    }

    val habitsFlow: Flow<List<Habit>> = context.dataStore.data.map { preferences ->
        val json = preferences[HABITS_KEY] ?: return@map emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveHabits(habits: List<Habit>) {
        context.dataStore.edit { preferences ->
            preferences[HABITS_KEY] = gson.toJson(habits)
        }
    }

    val statsFlow: Flow<Triple<Int, Int, Int>> = context.dataStore.data.map { preferences ->
        val vie = preferences[VIE_KEY]?.toIntOrNull() ?: 50
        val exp = preferences[EXP_KEY]?.toIntOrNull() ?: 12
        val mp = preferences[MP_KEY]?.toIntOrNull() ?: 15
        Triple(vie, exp, mp)
    }

    suspend fun saveStats(vie: Int, exp: Int, mp: Int) {
        context.dataStore.edit { preferences ->
            preferences[VIE_KEY] = vie.toString()
            preferences[EXP_KEY] = exp.toString()
            preferences[MP_KEY] = mp.toString()
        }
    }

    val profileImageUriFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PROFILE_IMAGE_KEY]
    }

    suspend fun saveProfileImageUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_KEY] = uri
        }
    }
}