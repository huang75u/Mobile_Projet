package com.example.mobile_projet.data.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class WeatherRepository(
    private val apiService: WeatherApiService = WeatherApiService.create()
) {
    

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

