package com.example.mobile_projet.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_projet.screens.SportGoal
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sharedPreferences = application.getSharedPreferences(
        "exercise_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    private val _sportGoals = MutableStateFlow<List<SportGoal>>(emptyList())
    val sportGoals: StateFlow<List<SportGoal>> = _sportGoals.asStateFlow()
    
    init {
        // 从 SharedPreferences 加载数据
        loadSportGoals()
    }
    
    private fun loadSportGoals() {
        val json = sharedPreferences.getString("sport_goals", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<SportGoal>>() {}.type
                val goals: List<SportGoal> = gson.fromJson(json, type)
                _sportGoals.value = goals
            } catch (e: Exception) {
                e.printStackTrace()
                _sportGoals.value = emptyList()
            }
        }
    }
    
    private fun saveSportGoals() {
        viewModelScope.launch {
            val json = gson.toJson(_sportGoals.value)
            sharedPreferences.edit()
                .putString("sport_goals", json)
                .apply()
        }
    }
    
    fun addSportGoal(goal: SportGoal) {
        _sportGoals.value = _sportGoals.value + goal
        saveSportGoals()
    }
    
    fun updateSportGoal(goalId: String, updatedGoal: SportGoal) {
        _sportGoals.value = _sportGoals.value.map {
            if (it.id == goalId) updatedGoal.copy(id = goalId) else it
        }
        saveSportGoals()
    }
    
    fun deleteSportGoal(goalId: String) {
        _sportGoals.value = _sportGoals.value.filter { it.id != goalId }
        saveSportGoals()
    }
    
    fun toggleSportGoalCompletion(goalId: String) {
        _sportGoals.value = _sportGoals.value.map {
            if (it.id == goalId) it.copy(isCompleted = !it.isCompleted) else it
        }
        saveSportGoals()
    }
}

