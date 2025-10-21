package com.example.mobile_projet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.mobile_projet.viewmodels.ExerciseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(viewModel: ExerciseViewModel = viewModel()) {
    val context = LocalContext.current
    val userPrefs = remember { UserDataManager.getUserPreferences(context) }
    
    // 实时监听用户数据
    var username by remember { mutableStateOf(userPrefs.username) }
    var points by remember { mutableStateOf(userPrefs.points) }
    
    // 监听运动目标和卡路里数据
    val sportGoals by viewModel.sportGoals.collectAsState()
    val dailyCalorieGoal by viewModel.dailyCalorieGoal.collectAsState()
    
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
                text = "Welcome~",
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
        
        // 天气区域（预留）
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Zone météo",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
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
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(weeklyGoals.size) { index ->
                        WeeklyGoalItem(weeklyGoals[index])
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
        modifier = Modifier.width(60.dp)
    ) {
        Text(
            text = dayName,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // 只在非未来日期时显示图标
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(60.dp)
        ) {
            if (!isFutureDay) {
                Image(
                    painter = painterResource(
                        id = if (dayStatus.achieved) R.drawable.ic_check else R.drawable.ic_cross
                    ),
                    contentDescription = if (dayStatus.achieved) "已完成" else "未完成",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

data class DayGoalStatus(
    val dayOffset: Int,
    val achieved: Boolean
)

