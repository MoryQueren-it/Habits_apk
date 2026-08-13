package com.example.habitudes

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesManager = PreferencesManager(application)

    val habits = MutableStateFlow<List<Habit>>(emptyList())
    val vieCurrent = MutableStateFlow(50)
    val expCurrent = MutableStateFlow(12)
    val mpCurrent = MutableStateFlow(15)
    val profileImageUri = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            val savedHabits = preferencesManager.habitsFlow.first()
            if (savedHabits.isNotEmpty()) {
                habits.value = savedHabits
            } else {
                habits.value = listOf(
                    Habit(1, "Méditer"),
                    Habit(2, "Boire de l'eau"),
                    Habit(3, "Sport"),
                    Habit(4, "Lire 20 pages"),
                    Habit(5, "Coder 1h"),
                    Habit(6, "Sommeil 8h")
                )
            }

            val (vie, exp, mp) = preferencesManager.statsFlow.first()
            vieCurrent.value = vie
            expCurrent.value = exp
            mpCurrent.value = mp

            val savedUri = preferencesManager.profileImageUriFlow.first()
            profileImageUri.value = savedUri
        }
    }

    private fun saveData() {
        viewModelScope.launch {
            preferencesManager.saveHabits(habits.value)
            preferencesManager.saveStats(vieCurrent.value, expCurrent.value, mpCurrent.value)
        }
    }

    // Copie physiquement la photo dans le stockage local de l'application
    // Cela évite la perte de la photo après fermeture due aux permissions temporaires Android
    fun updateProfileImageUri(sourceUri: Uri) {
        viewModelScope.launch {
            val permanentUriString = withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>()
                    val inputStream = context.contentResolver.openInputStream(sourceUri)
                    val file = File(context.filesDir, "profile_picture.jpg")
                    val outputStream = FileOutputStream(file)

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    Uri.fromFile(file).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    sourceUri.toString()
                }
            }

            profileImageUri.value = permanentUriString
            preferencesManager.saveProfileImageUri(permanentUriString)
        }
    }

    fun onPlusClicked(habitId: Int, vieMax: Int, expMax: Int, mpMax: Int) {
        habits.value = habits.value.map { habit ->
            if (habit.id == habitId) {
                habit.copy(plusCount = habit.plusCount + 1)
            } else {
                habit
            }
        }
        expCurrent.value = (expCurrent.value + 5).coerceAtMost(expMax)
        vieCurrent.value = (vieCurrent.value + 2).coerceAtMost(vieMax)
        mpCurrent.value = (mpCurrent.value + 2).coerceAtMost(mpMax)
        saveData()
    }

    fun onMinusClicked(habitId: Int) {
        habits.value = habits.value.map { habit ->
            if (habit.id == habitId) {
                habit.copy(minusCount = habit.minusCount + 1)
            } else {
                habit
            }
        }
        vieCurrent.value = (vieCurrent.value - 10).coerceAtLeast(0)
        mpCurrent.value = (mpCurrent.value - 5).coerceAtLeast(0)
        saveData()
    }

    fun addHabit(name: String) {
        val nextId = (habits.value.maxOfOrNull { it.id } ?: 0) + 1
        habits.value = habits.value + Habit(id = nextId, name = name.trim())
        saveData()
    }

    fun deleteHabit(habit: Habit) {
        habits.value = habits.value.filter { it.id != habit.id }
        saveData()
    }

    fun resetAll(initialVie: Int, initialExp: Int, initialMp: Int) {
        habits.value = habits.value.map { it.copy(plusCount = 0, minusCount = 0) }
        vieCurrent.value = initialVie
        expCurrent.value = initialExp
        mpCurrent.value = initialMp
        saveData()
    }
}