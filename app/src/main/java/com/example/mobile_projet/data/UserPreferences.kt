package com.example.mobile_projet.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_POINTS = "points"
        private const val KEY_TOTAL_POINTS = "total_points"
        private const val KEY_DAILY_POINTS = "daily_points"
        private const val KEY_STEPS = "steps"
        private const val KEY_LAST_ACTIVE_DATE = "last_active_date"
        private const val KEY_WEEKLY_GOAL_STATUS = "weekly_goal_status"
        private const val KEY_DAILY_CALORIES = "daily_calories"
        private const val DEFAULT_USERNAME = "Pseudo"
        private const val DEFAULT_STEPS = "3729"
    }
    
    var username: String
        get() = prefs.getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()
    
    // 历史累加的积分（不包括今天的）
    var historicalPoints: Int
        get() = prefs.getInt(KEY_TOTAL_POINTS, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_POINTS, value).apply()
    
    // 每日积分（每天清空）
    var dailyPoints: Int
        get() = prefs.getInt(KEY_DAILY_POINTS, 0)
        set(value) = prefs.edit().putInt(KEY_DAILY_POINTS, value).apply()
    
    // 总积分 = 历史积分 + 今日积分（实时计算，用于显示）
    var totalPoints: Int
        get() = historicalPoints + dailyPoints
        set(value) {
            // 设置总积分时，更新历史积分
            historicalPoints = value
        }
    
    // 为了向后兼容，保留points字段
    var points: Int
        get() = totalPoints
        set(value) {
            totalPoints = value
        }
    
    // 每日消耗的卡路里（每天清空）
    var dailyCalories: Double
        get() = prefs.getFloat(KEY_DAILY_CALORIES, 0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_DAILY_CALORIES, value.toFloat()).apply()
    
    var steps: String
        get() = prefs.getString(KEY_STEPS, DEFAULT_STEPS) ?: DEFAULT_STEPS
        set(value) = prefs.edit().putString(KEY_STEPS, value).apply()
    
    var lastActiveDate: String
        get() = prefs.getString(KEY_LAST_ACTIVE_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_ACTIVE_DATE, value).apply()
    
    // 每周目标完成状态（保存7天数据，key为星期几：1-7，value为是否完成）
    var weeklyGoalStatus: MutableMap<Int, Boolean>
        get() {
            val json = prefs.getString(KEY_WEEKLY_GOAL_STATUS, null)
            return if (json != null) {
                try {
                    val type = object : TypeToken<MutableMap<Int, Boolean>>() {}.type
                    gson.fromJson(json, type)
                } catch (e: Exception) {
                    mutableMapOf()
                }
            } else {
                mutableMapOf()
            }
        }
        set(value) {
            val json = gson.toJson(value)
            prefs.edit().putString(KEY_WEEKLY_GOAL_STATUS, json).apply()
        }
    
    fun addPoints(points: Int) {
        this.historicalPoints += points
    }
    
    // 将今日积分累加到历史积分，然后清空今日积分
    fun consolidateDailyPoints() {
        historicalPoints += dailyPoints
        dailyPoints = 0
    }
    
    // 清空每日数据
    fun clearDailyData() {
        dailyPoints = 0
        dailyCalories = 0.0
    }
}

