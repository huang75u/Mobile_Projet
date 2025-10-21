package com.example.mobile_projet.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobile_projet.R
import com.example.mobile_projet.viewmodels.ExerciseViewModel

@Composable
fun ExerciseScreen(
    navController: NavHostController,
    viewModel: ExerciseViewModel = viewModel()
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showCalorieGoalDialog by remember { mutableStateOf(false) }
    val sportGoals by viewModel.sportGoals.collectAsState()
    val dailyCalorieGoal by viewModel.dailyCalorieGoal.collectAsState()
    var editingGoal by remember { mutableStateOf<SportGoal?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 顶部标题栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧图标
                Icon(
                    painter = painterResource(id = R.drawable.exercise_quotidiens),
                    contentDescription = "Objectifs quotidiens",
                    tint = Color.Unspecified,  // 保持原图颜色
                    modifier = Modifier.size(90.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 标题文字
                Text(
                    text = "Objectifs quotidiens",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            // 卡路里统计卡片
            CaloriesCard(
                sportGoals = sportGoals,
                dailyGoal = dailyCalorieGoal,
                onGoalClick = { showCalorieGoalDialog = true },
                onRankingClick = {
                    // TODO: 实现排名功能
                }
            )
            
            // 主要内容区域 - 网格布局
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // 显示已完成的运动记录
            items(sportGoals) { goal ->
                SportGoalCard(
                    goal = goal,
                    onClick = {
                        editingGoal = goal
                        showGoalDialog = true
                    },
                    onDelete = {
                        viewModel.deleteSportGoal(goal.id)
                    }
                )
            }
            
            // 添加运动目标按钮卡片
            item {
                AddGoalCard(
                    onClick = {
                        editingGoal = null
                        showGoalDialog = true
                    }
                )
            }
        }  // 结束 LazyVerticalGrid
        }  // 结束主内容 Column
        
        // 右上角的积分规则按钮
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black,
                modifier = Modifier.size(32.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate("points_rules") },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "积分规则",
                        tint = Color.White,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Guide des\npoints",
                fontSize = 10.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
    
    // 运动目标设置对话框
    if (showGoalDialog) {
        Dialog(
            onDismissRequest = { 
                showGoalDialog = false
                editingGoal = null
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            SportGoalScreen(
                onDismiss = { 
                    showGoalDialog = false
                    editingGoal = null
                },
                onConfirm = { goal ->
                    if (editingGoal != null) {
                        // 编辑模式：更新现有记录
                        viewModel.updateSportGoal(editingGoal!!.id, goal)
                    } else {
                        // 新增模式：添加已完成的运动
                        viewModel.addSportGoal(goal)
                    }
                    showGoalDialog = false
                    editingGoal = null
                },
                initialGoal = editingGoal,
                existingSportGoals = sportGoals
            )
        }
    }
    
    // 设置每日卡路里目标对话框
    if (showCalorieGoalDialog) {
        SetDailyCalorieGoalDialog(
            currentGoal = dailyCalorieGoal,
            onDismiss = { showCalorieGoalDialog = false },
            onConfirm = { newGoal ->
                viewModel.setDailyCalorieGoal(newGoal)
                showCalorieGoalDialog = false
            }
        )
    }
}

@Composable
fun SportGoalCard(
    goal: SportGoal,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    
    // 已完成的运动卡片 - 橙色配色，匹配设计图
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFF9B4E))  // 橙色，匹配设计图
            .clickable { showOptionsDialog = true }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // 运动类型名称
            Text(
                text = goal.sportType,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            // 运动图标
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0x33000000)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = SportType.getIconForSport(goal.sportType)),
                    contentDescription = goal.sportType,
                    tint = Color.White,
                    modifier = Modifier.size(75.dp)
                )
            }
            
            // 目标数值和单位
            Text(
                text = "${goal.distance}${goal.unit}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
    
    // 选项对话框（编辑/删除）
    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = {
                Text(
                    text = goal.sportType,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Type: ${goal.sportType}")
                    Text("Niveau: ${goal.goalLevel}")
                    Text("Objectif: ${goal.distance} ${goal.unit}")
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = {
                            showOptionsDialog = false
                            onClick()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF007AFF)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Modifier")
                    }
                    
                    TextButton(
                        onClick = {
                            showOptionsDialog = false
                            onDelete()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFFEF5350)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Supprimer")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showOptionsDialog = false }) {
                    Text("Fermer")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// 添加运动目标的卡片按钮
@Composable
fun AddGoalCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFFB380))  // 浅橙色，匹配设计图
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "添加运动目标",
            tint = Color(0xFF8D6E63),  // 深灰褐色，匹配设计图
            modifier = Modifier.size(48.dp)
        )
    }
}

// 卡路里统计卡片
@Composable
fun CaloriesCard(
    sportGoals: List<SportGoal>,
    dailyGoal: Int,
    onGoalClick: () -> Unit = {},
    onRankingClick: () -> Unit = {}
) {
    // 计算已完成的总卡路里
    val completedCalories = sportGoals.sumOf { it.getCalories() }
    val percentage = if (dailyGoal > 0) {
        minOf((completedCalories / dailyGoal * 100).toInt(), 150)  // 上限150%
    } else {
        0
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFB0B0B0)  // 灰色背景，匹配设计图
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题栏和目标设置
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Calories brûlées",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    TextButton(
                        onClick = onGoalClick,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Objectif : ${dailyGoal} kcal ⚙️",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                TextButton(
                    onClick = onRankingClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Text("Classement »", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 主要内容区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧圆形进度图表
                CustomCircularProgressIndicator(
                    percentage = percentage,
                    size = 80.dp,
                    strokeWidth = 8.dp,
                    backgroundColor = Color(0xFF4A4A4A),  // 深灰色背景
                    progressColor = Color(0xFFFF9B4E)     // 橙色进度
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 右侧卡路里数值
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${completedCalories.toInt()} / ${dailyGoal}kcal",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722)  // 橙红色，类似设计图
                    )
                    Text(
                        text = if (sportGoals.isEmpty()) "Ajoutez vos exercices" 
                               else "${sportGoals.size} exercice(s) terminé(s)",
                        fontSize = 20.sp,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// 自定义圆形进度指示器
@Composable
fun CustomCircularProgressIndicator(
    percentage: Int,
    size: Dp = 100.dp,
    strokeWidth: Dp = 8.dp,
    backgroundColor: Color = Color.LightGray,
    progressColor: Color = Color(0xFFFF9F43)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        // 背景圆环和进度圆环
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            
            // 背景圆环
            drawArc(
                color = backgroundColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
            
            // 进度圆环
            val sweepAngle = (percentage / 100f) * 360f
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
        
        // 百分比文字
        Text(
            text = "$percentage%",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5722)  // 橙色文字，匹配设计图
        )
    }
}

// 设置每日卡路里目标对话框
@Composable
fun SetDailyCalorieGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goalInput by remember { mutableStateOf(currentGoal.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Définir l'objectif quotidien",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Combien de calories souhaitez-vous brûler aujourd'hui ?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { newValue ->
                        // 只允许输入数字
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            goalInput = newValue
                        }
                    },
                    label = { Text("Objectif (kcal)") },
                    suffix = { Text("kcal", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                
                Text(
                    text = "* Recommandé : 300-800 kcal par jour",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                
                // 快捷选择按钮
                Text(
                    text = "Objectifs suggérés :",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(300, 500, 800).forEach { preset ->
                        Button(
                            onClick = { goalInput = preset.toString() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (goalInput == preset.toString()) 
                                    Color(0xFFFF9F43) else Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${preset}",
                                color = if (goalInput == preset.toString()) 
                                    Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = goalInput.toIntOrNull()
                    if (goal != null && goal > 0) {
                        onConfirm(goal)
                    }
                },
                enabled = goalInput.toIntOrNull() != null && goalInput.toIntOrNull()!! > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF27AE60)
                )
            ) {
                Text("Confirmer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

