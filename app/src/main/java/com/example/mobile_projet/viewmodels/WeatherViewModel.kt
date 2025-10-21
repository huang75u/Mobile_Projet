package com.example.mobile_projet.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_projet.data.weather.WeatherData
import com.example.mobile_projet.data.weather.WeatherRepository
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
    
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()
    
    init {
        // 默认加载巴黎的天气（可以改成其他城市）
        loadWeather("Metz")
    }
    
    /**
     * 根据城市名称加载天气
     */
    fun loadWeather(cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            
            val result = repository.getWeatherByCity(cityName)
            
            _weatherState.value = if (result.isSuccess) {
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

