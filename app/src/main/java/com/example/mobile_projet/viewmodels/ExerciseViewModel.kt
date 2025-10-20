package com.example.mobile_projet.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_projet.data.UserDataManager
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
    private val userPrefs = UserDataManager.getUserPreferences(application)
    
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
        calculateAndUpdatePoints()
    }
    
    // 积分计算规则
    private fun calculateAndUpdatePoints() {
        val goals = _sportGoals.value
        val totalCalories = goals.sumOf { it.getCalories() }
        val completedCalories = goals.filter { it.isCompleted }.sumOf { it.getCalories() }
        
        if (totalCalories == 0.0) {
            userPrefs.points = 0
            return
        }
        
        val completionPercentage = (completedCalories / totalCalories * 100).toInt()
        var points = 0
        
        // 规则1：完成单个运动项目奖励
        // 根据卡路里量给积分：每10卡路里 = 1积分
        goals.filter { it.isCompleted }.forEach { goal ->
            val calories = goal.getCalories()
            points += (calories / 10).toInt()
        }
        
        // 规则2：总体完成百分比里程碑奖励
        when {
            completionPercentage >= 100 -> points += 100  // 完成100%：额外100积分
            completionPercentage >= 75 -> points += 50    // 完成75%：额外50积分
            completionPercentage >= 50 -> points += 30    // 完成50%：额外30积分
            completionPercentage >= 25 -> points += 10    // 完成25%：额外10积分
        }
        
        // 规则3：全部完成奖励
        val completedCount = goals.count { it.isCompleted }
        if (completedCount == goals.size && goals.isNotEmpty()) {
            points += 50  // 完成所有项目：额外50积分
        }
        
        // 规则4：多项目完成组合奖励
        if (completedCount >= 3) {
            points += completedCount * 5  // 每完成3个或以上项目，每个额外5积分
        }
        
        // 更新积分
        userPrefs.points = points
    }
}

