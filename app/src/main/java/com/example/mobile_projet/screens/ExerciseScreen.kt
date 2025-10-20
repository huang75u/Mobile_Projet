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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mobile_projet.viewmodels.ExerciseViewModel

@Composable
fun ExerciseScreen(
    navController: NavHostController,
    viewModel: ExerciseViewModel = viewModel()
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    val sportGoals by viewModel.sportGoals.collectAsState()
    var editingGoal by remember { mutableStateOf<SportGoal?>(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 卡路里统计卡片（顶部）
            CaloriesCard(
                sportGoals = sportGoals,
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
            // 显示已添加的运动目标卡片
            items(sportGoals) { goal ->
                SportGoalCard(
                    goal = goal,
                    onClick = {
                        editingGoal = goal
                        showGoalDialog = true
                    },
                    onDelete = {
                        viewModel.deleteSportGoal(goal.id)
                    },
                    onToggleComplete = {
                        viewModel.toggleSportGoalCompletion(goal.id)
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
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Black,
                modifier = Modifier.size(56.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate("points_rules") },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "积分规则",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Guide des\npoints",
                fontSize = 10.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
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
            // 获取已使用的运动类型，用于过滤
            val usedSportTypes = sportGoals
                .filter { it.id != editingGoal?.id } // 编辑模式下排除当前编辑的项
                .map { it.sportType }
                .toSet()
            
            SportGoalScreen(
                onDismiss = { 
                    showGoalDialog = false
                    editingGoal = null
                },
                onConfirm = { goal ->
                    if (editingGoal != null) {
                        // 编辑模式：更新现有目标
                        viewModel.updateSportGoal(editingGoal!!.id, goal)
                    } else {
                        // 新增模式：添加新目标
                        viewModel.addSportGoal(goal)
                    }
                    showGoalDialog = false
                    editingGoal = null
                },
                initialGoal = editingGoal,
                excludedSportTypes = usedSportTypes
            )
        }
    }
}

@Composable
fun SportGoalCard(
    goal: SportGoal,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit = {}
) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    
    // 运动卡片，根据完成状态显示不同颜色 - 多巴胺配色
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (goal.isCompleted) Color(0xFFFF9F43)  // 完成：活力橙
                else Color(0xFFBA68C8)                    // 未完成：梦幻紫
            )
            .clickable { showOptionsDialog = true }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 完成状态标记（右上角）
        if (goal.isCompleted) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF27AE60)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // 运动类型名称
            Text(
                text = goal.sportType,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            // 图标占位符（留给用户添加图标）
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0x33000000)),
                contentAlignment = Alignment.Center
            ) {
                // TODO: 在这里添加运动图标
                // Icon(painter = painterResource(id = R.drawable.icon_name), ...)
                Text(
                    text = "🏃", // 临时占位符
                    fontSize = 32.sp
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 完成状态切换按钮
                    Button(
                        onClick = {
                            onToggleComplete()
                            showOptionsDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (goal.isCompleted) Color(0xFFE0E0E0) else Color(0xFF27AE60)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = if (goal.isCompleted) Icons.Default.Delete else Icons.Default.Add,
                            contentDescription = if (goal.isCompleted) "标记为未完成" else "标记为完成",
                            modifier = Modifier.size(20.dp),
                            tint = if (goal.isCompleted) Color.Gray else Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (goal.isCompleted) "Marquer comme non fait" else "Marquer comme fait",
                            color = if (goal.isCompleted) Color.Gray else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                showOptionsDialog = false
                                onClick()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF007AFF)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "编辑",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Modifier")
                        }
                        
                        TextButton(
                            onClick = {
                                showOptionsDialog = false
                                onDelete()
                            },
                            modifier = Modifier.weight(1f),
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

// 添加运动目标的卡片按钮 - 多巴胺配色
@Composable
fun AddGoalCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFF6B9D))  // 甜蜜粉
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "添加运动目标",
            tint = Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}

// 卡路里统计卡片
@Composable
fun CaloriesCard(
    sportGoals: List<SportGoal>,
    onRankingClick: () -> Unit = {}
) {
    val totalCalories = sportGoals.sumOf { it.getCalories() }
    val completedCalories = sportGoals.filter { it.isCompleted }.sumOf { it.getCalories() }
    val percentage = if (totalCalories > 0) {
        (completedCalories / totalCalories * 100).toInt()
    } else {
        0
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF81D4FA)  // 天空蓝
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                    Text(
                    text = "Calories brûlées",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                TextButton(
                    onClick = onRankingClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
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
                    backgroundColor = Color(0xFFFFE082),  // 亮黄色背景
                    progressColor = Color(0xFFFF9F43)     // 橙色进度
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 右侧卡路里数值
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${completedCalories.toInt()} / ${totalCalories.toInt()}kcal",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (sportGoals.isEmpty()) "Ajoutez des objectifs" 
                               else "${sportGoals.count { it.isCompleted }}/${sportGoals.size} complétés",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
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
            color = Color.White
        )
    }
}

