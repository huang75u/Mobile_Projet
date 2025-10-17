package com.example.mobile_projet.data

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_POINTS = "points"
        private const val KEY_STEPS = "steps"
        private const val DEFAULT_USERNAME = "Pseudo"
        private const val DEFAULT_STEPS = "3729"
    }
    
    var username: String
        get() = prefs.getString(KEY_USERNAME, DEFAULT_USERNAME) ?: DEFAULT_USERNAME
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()
    
    var points: Int
        get() = prefs.getInt(KEY_POINTS, 0)
        set(value) = prefs.edit().putInt(KEY_POINTS, value).apply()
    
    var steps: String
        get() = prefs.getString(KEY_STEPS, DEFAULT_STEPS) ?: DEFAULT_STEPS
        set(value) = prefs.edit().putString(KEY_STEPS, value).apply()
    
    fun addPoints(points: Int) {
        this.points += points
    }
}

