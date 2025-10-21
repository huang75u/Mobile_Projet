package com.example.mobile_projet.data.weather

import com.google.gson.annotations.SerializedName

/**
 * OpenWeatherMap API 响应数据模型
 */
data class WeatherResponse(
    @SerializedName("main")
    val main: Main,
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("name")
    val cityName: String
)

data class Main(
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("humidity")
    val humidity: Int
)

data class Weather(
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

data class Wind(
    @SerializedName("speed")
    val speed: Double
)

/**
 * UI 展示用的天气数据模型
 */
data class WeatherData(
    val temperature: Int,
    val tempMin: Int,
    val tempMax: Int,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val cityName: String,
    val weatherIcon: String
) {
    companion object {
        fun fromWeatherResponse(response: WeatherResponse): WeatherData {
            return WeatherData(
                temperature = response.main.temp.toInt(),
                tempMin = response.main.tempMin.toInt(),
                tempMax = response.main.tempMax.toInt(),
                description = response.weather.firstOrNull()?.description ?: "",
                humidity = response.main.humidity,
                windSpeed = response.wind.speed,
                cityName = response.cityName,
                weatherIcon = response.weather.firstOrNull()?.icon ?: "01d"
            )
        }
    }
    
    // 获取天气图标的emoji表示
    fun getWeatherEmoji(): String {
        return when {
            weatherIcon.startsWith("01") -> "☀️"  // Clear sky
            weatherIcon.startsWith("02") -> "⛅"  // Few clouds
            weatherIcon.startsWith("03") -> "☁️"  // Scattered clouds
            weatherIcon.startsWith("04") -> "☁️"  // Broken clouds
            weatherIcon.startsWith("09") -> "🌧️"  // Shower rain
            weatherIcon.startsWith("10") -> "🌦️"  // Rain
            weatherIcon.startsWith("11") -> "⛈️"  // Thunderstorm
            weatherIcon.startsWith("13") -> "❄️"  // Snow
            weatherIcon.startsWith("50") -> "🌫️"  // Mist
            else -> "🌤️"
        }
    }
}

