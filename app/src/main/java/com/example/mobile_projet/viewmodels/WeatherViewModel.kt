package com.example.mobile_projet.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_projet.data.weather.WeatherData
import com.example.mobile_projet.data.weather.WeatherRepository
import com.example.mobile_projet.data.UserDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 天气状态
 */
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val weatherData: WeatherData) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

/**
 * 天气ViewModel
 * 管理天气数据的获取和状态
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = WeatherRepository()
    private val userPrefs = UserDataManager.getUserPreferences(application)
    
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()
    
    init {
        // 优先加载上次保存的位置
        val lat = userPrefs.lastWeatherLatitude
        val lon = userPrefs.lastWeatherLongitude
        val city = userPrefs.lastWeatherCity
        if (lat != 0.0 && lon != 0.0) {
            loadWeatherByLocation(lat, lon)
        } else if (city.isNotBlank()) {
            loadWeather(city)
        } else {
            loadWeather("Metz")
        }
    }
    
    /**
     * 根据城市名称加载天气
     */
    fun loadWeather(cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            
            val result = repository.getWeatherByCity(cityName)
            
            _weatherState.value = if (result.isSuccess) {
                // 保存城市
                userPrefs.lastWeatherCity = cityName
                userPrefs.lastWeatherLatitude = 0.0
                userPrefs.lastWeatherLongitude = 0.0
                WeatherUiState.Success(result.getOrNull()!!)
            } else {
                WeatherUiState.Error(
                    result.exceptionOrNull()?.message ?: "Erreur de chargement de la météo"
                )
            }
        }
    }
    
    /**
     * 根据经纬度加载天气
     */
    fun loadWeatherByLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            
            val result = repository.getWeatherByCoordinates(latitude, longitude)
            
            _weatherState.value = if (result.isSuccess) {
                // 保存经纬度
                userPrefs.lastWeatherLatitude = latitude
                userPrefs.lastWeatherLongitude = longitude
                userPrefs.lastWeatherCity = ""
                WeatherUiState.Success(result.getOrNull()!!)
            } else {
                WeatherUiState.Error(
                    result.exceptionOrNull()?.message ?: "Erreur de chargement de la météo"
                )
            }
        }
    }
    
    /**
     * 刷新天气数据
     */
    fun refreshWeather(cityName: String = "Paris") {
        loadWeather(cityName)
    }
}

