package com.example.mobile_projet.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mobile_projet.R
import com.example.mobile_projet.data.ImageStorage
import com.example.mobile_projet.data.UserDataManager
import com.example.mobile_projet.utils.LocationHelper
import com.example.mobile_projet.viewmodels.ExerciseViewModel
import com.example.mobile_projet.viewmodels.WeatherViewModel
import com.example.mobile_projet.viewmodels.WeatherUiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
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
    var isRefreshing by remember { mutableStateOf(false) }
    
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
    
    // 检查每日数据更新
    LaunchedEffect(Unit) {
        exerciseViewModel.checkAndResetDailyData()
    }
    
    // 实时更新用户数据
    LaunchedEffect(Unit) {
        while (true) {
            username = userPrefs.username
            points = userPrefs.points
            kotlinx.coroutines.delay(1000) // 每秒检查一次
        }
    }
    
    // 从UserPreferences读取每日卡路里（实时数据）
    val todayCalories = remember {
        derivedStateOf { userPrefs.dailyCalories }
    }.value
    
    // 获取最近7天的数据（从6天前到今天），今天永远在最右侧
    val weeklyGoals = remember(sportGoals, dailyCalorieGoal) {
        val weeklyStatus = userPrefs.weeklyGoalStatus
        val df = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
        // 生成从6天前到今天的7天数据
        (6 downTo 0).map { dayOffset ->
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -dayOffset)
            val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
            val key = df.format(calendar.time)
            // 对于今天（dayOffset = 0），实时计算是否完成目标
            val achieved = if (dayOffset == 0) {
                val todayCaloriesValue = sportGoals.sumOf { it.getCalories() }
                todayCaloriesValue >= dailyCalorieGoal
            } else {
                weeklyStatus[key] ?: false
            }
            DayGoalStatus(
                dayOfWeek = dayOfWeek,
                achieved = achieved,
                isToday = (dayOffset == 0)
            )
        }
    }
    
    // 获取头像文件
    var avatarImageFile by remember { mutableStateOf(ImageStorage.getAvatarImageFile(context)) }
    var avatarImageKey by remember { mutableStateOf(0) }
    
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                exerciseViewModel.refreshData()
                // 同时刷新用户数据显示
                username = userPrefs.username
                points = userPrefs.points
                isRefreshing = false
            }
        }
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 顶部区域 - Welcome和用户昵称+头像
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bienvenue~",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 刷新按钮
                IconButton(
                    onClick = {
                        scope.launch {
                            isRefreshing = true
                            exerciseViewModel.refreshData()
                            username = userPrefs.username
                            points = userPrefs.points
                            isRefreshing = false
                        }
                    },
                    enabled = !isRefreshing,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rafraîchir",
                        tint = if (isRefreshing) Color.Gray else Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // 昵称和头像
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // 头像
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF2196F3), CircleShape)
                ) {
                    if (avatarImageFile != null) {
                        key(avatarImageKey) {
                            AsyncImage(
                                model = avatarImageFile,
                                contentDescription = "头像",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        // 默认头像
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF9C89CC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_profile),
                                contentDescription = "默认头像",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = username,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

            }
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
                        text = "${todayCalories.toInt()}K",
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
    }  // 结束 Column
    }  // 结束 PullToRefreshBox
}

// 每周目标单项
@Composable
fun WeeklyGoalItem(dayStatus: DayGoalStatus) {
    val dayName = when (dayStatus.dayOfWeek) {
        Calendar.SUNDAY -> "Dim"
        Calendar.MONDAY -> "Lun"
        Calendar.TUESDAY -> "Mar"
        Calendar.WEDNESDAY -> "Mer"
        Calendar.THURSDAY -> "Jeu"
        Calendar.FRIDAY -> "Ven"
        Calendar.SATURDAY -> "Sam"
        else -> ""
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(45.dp)
    ) {
        Text(
            text = dayName,
            fontSize = 11.sp,
            color = if (dayStatus.isToday) Color(0xFF2196F3) else Color.Gray,
            fontWeight = if (dayStatus.isToday) FontWeight.Bold else FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // 显示完成状态图标
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)
        ) {
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

data class DayGoalStatus(
    val dayOfWeek: Int, // 1-7: 周日到周六
    val achieved: Boolean,
    val isToday: Boolean = false
)

