package com.example.mobile_projet.data.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 天气数据仓库
 * 负责从API获取天气数据
 */
class WeatherRepository(
    private val apiService: WeatherApiService = WeatherApiService.create()
) {
    
    /**
     * 根据城市名称获取天气数据
     */
    suspend fun getWeatherByCity(cityName: String): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentWeather(
                    cityName = cityName,
                    apiKey = WeatherApiService.API_KEY
                )
                Result.success(WeatherData.fromWeatherResponse(response))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 根据经纬度获取天气数据
     */
    suspend fun getWeatherByCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentWeatherByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                    apiKey = WeatherApiService.API_KEY
                )
                Result.success(WeatherData.fromWeatherResponse(response))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

