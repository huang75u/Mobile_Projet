package com.example.mobile_projet.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_projet.R
import com.example.mobile_projet.data.UserDataManager
import com.example.mobile_projet.utils.LocationHelper
import com.example.mobile_projet.viewmodels.ExerciseViewModel
import com.example.mobile_projet.viewmodels.WeatherViewModel
import com.example.mobile_projet.viewmodels.WeatherUiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    exerciseViewModel: ExerciseViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val userPrefs = remember { UserDataManager.getUserPreferences(context) }
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }
    
    // 实时监听用户数据
    var username by remember { mutableStateOf(userPrefs.username) }
    var points by remember { mutableStateOf(userPrefs.points) }
    
    // 监听运动目标和卡路里数据
    val sportGoals by exerciseViewModel.sportGoals.collectAsState()
    val dailyCalorieGoal by exerciseViewModel.dailyCalorieGoal.collectAsState()
    
    // 监听天气数据
    val weatherState by weatherViewModel.weatherState.collectAsState()
    
    // 位置权限请求
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            // 权限已授予，获取位置并加载天气
            scope.launch {
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    weatherViewModel.loadWeatherByLocation(location.first, location.second)
                }
            }
        }
    }
    
    // 实时更新用户数据
    LaunchedEffect(Unit) {
        while (true) {
            username = userPrefs.username
            points = userPrefs.points
            kotlinx.coroutines.delay(1000) // 每秒检查一次
        }
    }
    
    // 计算今日卡路里消耗总量
    val todayCalories = remember(sportGoals) {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        sportGoals.filter { goal ->
            val goalDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(goal.timestamp))
            goalDate == today
        }.sumOf { it.getCalories() }
    }
    
    // 获取最近7天的数据（从6天前到今天）
    val weeklyGoals = remember(sportGoals, dailyCalorieGoal) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        
        (6 downTo 0).map { dayOffset ->
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)
            val dateStr = dateFormat.format(calendar.time)
            
            val dayCalories = sportGoals.filter { goal ->
                val goalDate = dateFormat.format(Date(goal.timestamp))
                goalDate == dateStr
            }.sumOf { it.getCalories() }
            
            DayGoalStatus(
                dayOffset = dayOffset,
                achieved = dayCalories >= dailyCalorieGoal
            )
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部区域 - Welcome和用户昵称
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bienvenue~",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = username,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 天气区域
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            when (val state = weatherState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = Color(0xFF2196F3)
                        )
                    }
                }
                is WeatherUiState.Success -> {
                    // 获取当前日期和星期
                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
                    val dayFormat = SimpleDateFormat("EEEE", Locale.FRENCH)
                    val currentDate = dateFormat.format(calendar.time)
                    val currentDay = dayFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
                    
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            // 顶部：日期和星期
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = currentDate,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    text = currentDay,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // 中间：天气主要信息
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 左侧：温度和天气图标
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = state.weatherData.getWeatherEmoji(),
                                        fontSize = 44.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${state.weatherData.temperature}°C",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = state.weatherData.description.replaceFirstChar { 
                                                it.uppercase() 
                                            },
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                
                                // 右侧：详细天气信息
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    // 城市名和位置按钮在同一行
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = state.weatherData.cityName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        // 位置按钮
                                        IconButton(
                                            onClick = {
                                                if (locationHelper.hasLocationPermission()) {
                                                    // 已有权限，直接获取位置
                                                    scope.launch {
                                                        val location = locationHelper.getCurrentLocation()
                                                        if (location != null) {
                                                            weatherViewModel.loadWeatherByLocation(location.first, location.second)
                                                        }
                                                    }
                                                } else {
                                                    // 请求权限
                                                    locationPermissionLauncher.launch(LocationHelper.LOCATION_PERMISSIONS)
                                                }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = "获取当前位置天气",
                                                modifier = Modifier.size(20.dp),
                                                tint = Color(0xFF2196F3)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "↑${state.weatherData.tempMax}°",
                                            fontSize = 13.sp,
                                            color = Color(0xFFFF6B6B),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "↓${state.weatherData.tempMin}°",
                                            fontSize = 13.sp,
                                            color = Color(0xFF4ECDC4),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    // 湿度和风力在同一行
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "💧 ${state.weatherData.humidity}%",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "💨 ${state.weatherData.windSpeed} m/s",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Météo indisponible",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 每周训练目标区域 - 添加背景卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 每周训练目标标题
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_weekly_goal),
                        contentDescription = "每周目标",
                        modifier = Modifier.size(65.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Objectifs d'entraînement hebdomadaires",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 每周目标显示（7天）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    weeklyGoals.forEach { dayGoal ->
                        WeeklyGoalItem(dayGoal)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 底部卡片 - 卡路里和积分
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 卡路里卡片
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_calories),
                        contentDescription = "卡路里",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
        Text(
                        text = "${todayCalories.toInt()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
        Text(
                        text = "Calories",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // 积分卡片
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_points),
                        contentDescription = "积分",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$points",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Points",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// 每周目标单项
@Composable
fun WeeklyGoalItem(dayStatus: DayGoalStatus) {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -dayStatus.dayOffset)
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val dayNames = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
    val dayName = when (dayOfWeek) {
        Calendar.SUNDAY -> "Dim"
        Calendar.MONDAY -> "Lun"
        Calendar.TUESDAY -> "Mar"
        Calendar.WEDNESDAY -> "Mer"
        Calendar.THURSDAY -> "Jeu"
        Calendar.FRIDAY -> "Ven"
        Calendar.SATURDAY -> "Sam"
        else -> ""
    }
    
    // 判断是否是未来的日期
    val isFutureDay = dayStatus.dayOffset < 0
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(45.dp)
    ) {
        Text(
            text = dayName,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // 只在非未来日期时显示图标
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)
        ) {
            if (!isFutureDay) {
                Image(
                    painter = painterResource(
                        id = if (dayStatus.achieved) R.drawable.ic_check else R.drawable.ic_cross
                    ),
                    contentDescription = if (dayStatus.achieved) "已完成" else "未完成",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

data class DayGoalStatus(
    val dayOffset: Int,
    val achieved: Boolean
)

