package com.example.mobile_projet.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SportGoal(
    val sportType: String,
    val goalLevel: String,
    val distance: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportGoalScreen(
    onDismiss: () -> Unit,
    onConfirm: (SportGoal) -> Unit
) {
    var selectedSport by remember { mutableStateOf("") }
    var expandedSportMenu by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf("") }
    var customDistance by remember { mutableStateOf("") }
    
    val sportTypes = listOf(
        "Course à pied",
        "Vélo",
        "Natation",
        "Marche",
        "Yoga",
        "Musculation"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 下拉菜单选择运动类型
            ExposedDropdownMenuBox(
                expanded = expandedSportMenu,
                onExpandedChange = { expandedSportMenu = !expandedSportMenu }
            ) {
                OutlinedTextField(
                    value = selectedSport,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Choisissez un sport") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "下拉菜单"
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                
                ExposedDropdownMenu(
                    expanded = expandedSportMenu,
                    onDismissRequest = { expandedSportMenu = false }
                ) {
                    sportTypes.forEach { sport ->
                        DropdownMenuItem(
                            text = { Text(sport) },
                            onClick = {
                                selectedSport = sport
                                expandedSportMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 2. 标题文本
            Text(
                text = "Choisissez votre objectif sportif quotidien :",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 3. 三个运动等级按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Débutant - 1km
                GoalButton(
                    label = "Débutant",
                    distance = "1km",
                    isSelected = selectedGoal == "1km",
                    onClick = {
                        selectedGoal = "1km"
                        customDistance = ""
                    },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Standard - 2km
                GoalButton(
                    label = "Standard",
                    distance = "2km",
                    isSelected = selectedGoal == "2km",
                    onClick = {
                        selectedGoal = "2km"
                        customDistance = ""
                    },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Défi - 4km
                GoalButton(
                    label = "Défi",
                    distance = "4km",
                    isSelected = selectedGoal == "4km",
                    onClick = {
                        selectedGoal = "4km"
                        customDistance = ""
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 4. 自定义运动量输入框
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ou",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 16.dp)
                )
                
                OutlinedTextField(
                    value = customDistance,
                    onValueChange = { 
                        customDistance = it
                        if (it.isNotEmpty()) {
                            selectedGoal = ""
                        }
                    },
                    placeholder = { Text("Personnaliser") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFFB74D),
                        unfocusedContainerColor = Color(0xFFFFB74D),
                        focusedBorderColor = Color(0xFFFF9800),
                        unfocusedBorderColor = Color(0xFFFF9800)
                    ),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 5. 取消和确认按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 取消按钮（红色）
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF9A9A)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Annuler",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 确认按钮（绿色）
                Button(
                    onClick = {
                        val finalDistance = if (customDistance.isNotEmpty()) {
                            customDistance
                        } else {
                            selectedGoal
                        }
                        
                        if (selectedSport.isNotEmpty() && finalDistance.isNotEmpty()) {
                            val goalLevel = when (selectedGoal) {
                                "1km" -> "Débutant"
                                "2km" -> "Standard"
                                "4km" -> "Défi"
                                else -> "Personnalisé"
                            }
                            onConfirm(SportGoal(selectedSport, goalLevel, finalDistance))
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedSport.isNotEmpty() && 
                             (selectedGoal.isNotEmpty() || customDistance.isNotEmpty())
                ) {
                    Text(
                        text = "Confirmer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun GoalButton(
    label: String,
    distance: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标签文字
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        // 按钮
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Color(0xFFFF9800) else Color(0xFFFFB74D)
            ),
            shape = RoundedCornerShape(12.dp),
            border = if (isSelected) BorderStroke(2.dp, Color(0xFFFF6F00)) else null,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (isSelected) 8.dp else 2.dp
            )
        ) {
            Text(
                text = distance,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

