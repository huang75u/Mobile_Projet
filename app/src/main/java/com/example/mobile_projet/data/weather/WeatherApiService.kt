package com.example.mobile_projet.data.weather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * OpenWeatherMap API 接口
 * 
 * 使用说明：
 * 1. 注册账号：https://openweathermap.org/
 * 2. 获取免费API Key：https://home.openweathermap.org/api_keys
 * 3. 将 API_KEY 替换为你的实际 API Key
 */
interface WeatherApiService {
    
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",  // metric = 摄氏度
        @Query("lang") lang: String = "fr"         // 法语
    ): WeatherResponse
    
    @GET("weather")
    suspend fun getCurrentWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "fr"
    ): WeatherResponse
    
    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        
        // 请在这里填入你的 OpenWeatherMap API Key
        // 注册地址: https://openweathermap.org/api
        const val API_KEY = "68b07dd3826b224912faac7fb0389154" 
        
        fun create(): WeatherApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            return retrofit.create(WeatherApiService::class.java)
        }
    }
}

