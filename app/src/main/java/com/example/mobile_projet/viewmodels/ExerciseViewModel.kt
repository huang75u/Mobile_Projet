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
import com.example.mobile_projet.data.firebase.FriendsRepository

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sharedPreferences = application.getSharedPreferences(
        "exercise_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val userPrefs = UserDataManager.getUserPreferences(application)
    private val repo = FriendsRepository()
    
    private val _sportGoals = MutableStateFlow<List<SportGoal>>(emptyList())
    val sportGoals: StateFlow<List<SportGoal>> = _sportGoals.asStateFlow()
    
    private val _dailyCalorieGoal = MutableStateFlow(500)
    val dailyCalorieGoal: StateFlow<Int> = _dailyCalorieGoal.asStateFlow()
    
    init {
        // 从 SharedPreferences 加载数据
        loadSportGoals()
        loadDailyCalorieGoal()
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
    
    private fun loadDailyCalorieGoal() {
        _dailyCalorieGoal.value = sharedPreferences.getInt("daily_calorie_goal", 500)
    }
    
    fun setDailyCalorieGoal(goal: Int) {
        _dailyCalorieGoal.value = goal
        sharedPreferences.edit()
            .putInt("daily_calorie_goal", goal)
            .apply()
        calculateAndUpdatePoints()
    }
    
    fun addSportGoal(goal: SportGoal) {
        _sportGoals.value = _sportGoals.value + goal
        saveSportGoals()
        calculateAndUpdatePoints()
        // 同步到 Firebase
        viewModelScope.launch {
            try {
                val uid = repo.signInAnonymouslyIfNeeded()
                // 以本地 goal.id 作为 docId，后续可更新/删除
                repo.upsertActivity(uid, goal.id, goal.sportType, goal.getCalories().toInt(), System.currentTimeMillis())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun updateSportGoal(goalId: String, updatedGoal: SportGoal) {
        _sportGoals.value = _sportGoals.value.map {
            if (it.id == goalId) updatedGoal.copy(id = goalId) else it
        }
        saveSportGoals()
        calculateAndUpdatePoints()
        // 同步到 Firebase（更新）
        viewModelScope.launch {
            try {
                val uid = repo.signInAnonymouslyIfNeeded()
                repo.upsertActivity(uid, goalId, updatedGoal.sportType, updatedGoal.getCalories().toInt(), System.currentTimeMillis())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun deleteSportGoal(goalId: String) {
        _sportGoals.value = _sportGoals.value.filter { it.id != goalId }
        saveSportGoals()
        calculateAndUpdatePoints()
        // 同步到 Firebase（删除）
        viewModelScope.launch {
            try {
                val uid = repo.signInAnonymouslyIfNeeded()
                repo.deleteActivity(uid, goalId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // 新的积分计算规则
    private fun calculateAndUpdatePoints() {
        val completedExercises = _sportGoals.value
        val dailyGoal = _dailyCalorieGoal.value
        
        // 计算已完成的总卡路里
        val totalCompletedCalories = completedExercises.sumOf { it.getCalories() }
        
        if (dailyGoal == 0) {
            userPrefs.points = 0
            return
        }
        
        // 计算完成百分比
        val completionPercentage = (totalCompletedCalories / dailyGoal * 100).toInt()
        var points = 0
        
        // 规则1：基础积分 - 每完成一个运动项目
        // 根据卡路里量给积分：每10卡路里 = 1积分
        completedExercises.forEach { exercise ->
            val calories = exercise.getCalories()
            points += (calories / 10).toInt()
        }
        
        // 规则2：每日目标完成度里程碑奖励
        when {
            completionPercentage >= 100 -> {
                points += 100  // 完成100%：100积分
                
                // 规则3：超额完成奖励（上限150%）
                val excessPercentage = minOf(completionPercentage - 100, 50)
                points += excessPercentage * 2  // 每超1%额外2积分，最多100积分
            }
            completionPercentage >= 80 -> points += 60   // 完成80%：60积分
            completionPercentage >= 60 -> points += 40   // 完成60%：40积分
            completionPercentage >= 40 -> points += 20   // 完成40%：20积分
            completionPercentage >= 20 -> points += 10   // 完成20%：10积分
        }
        
        // 规则4：多样性奖励 - 完成3种或以上不同运动
        val exerciseTypeCount = completedExercises.map { it.sportType }.toSet().size
        if (exerciseTypeCount >= 3) {
            points += exerciseTypeCount * 8  // 每种运动类型8积分
        }
        
        // 规则5：运动量下限保护 - 至少完成20%才有积分
        if (completionPercentage < 20) {
            points = 0
        }
        
        // 更新积分
        userPrefs.points = points
    }
}

