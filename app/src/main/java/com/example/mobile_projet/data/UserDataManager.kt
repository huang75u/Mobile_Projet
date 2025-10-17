package com.example.mobile_projet.data

import android.content.Context

/**
 * 全局用户数据管理器
 * 用于在不同界面间共享用户数据
 */
object UserDataManager {
    private var userPreferences: UserPreferences? = null
    
    fun init(context: Context) {
        if (userPreferences == null) {
            userPreferences = UserPreferences(context.applicationContext)
        }
    }
    
    fun getUserPreferences(context: Context): UserPreferences {
        if (userPreferences == null) {
            init(context)
        }
        return userPreferences!!
    }
    
    fun addPoints(context: Context, points: Int) {
        getUserPreferences(context).addPoints(points)
    }
    
    fun getPoints(context: Context): Int {
        return getUserPreferences(context).points
    }
    
    fun getUsername(context: Context): String {
        return getUserPreferences(context).username
    }
}

