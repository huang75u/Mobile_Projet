package com.example.mobile_projet.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController

@Composable
fun ExerciseScreen(navController: NavHostController) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var sportGoals by remember { mutableStateOf(listOf<SportGoal>()) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 主要内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 添加运动目标按钮
            Button(
                onClick = { showGoalDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ajouter un objectif sportif",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 显示已添加的运动目标列表
            if (sportGoals.isEmpty()) {
                Text(
                    text = "Aucun objectif sportif ajouté",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sportGoals) { goal ->
                        SportGoalCard(goal)
                    }
                }
            }
        }
        
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
            onDismissRequest = { showGoalDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            SportGoalScreen(
                onDismiss = { showGoalDialog = false },
                onConfirm = { goal ->
                    sportGoals = sportGoals + goal
                    showGoalDialog = false
                }
            )
        }
    }
}

@Composable
fun SportGoalCard(goal: SportGoal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.sportType,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Niveau: ${goal.goalLevel}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFB74D)
                ) {
                    Text(
                        text = goal.distance,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

