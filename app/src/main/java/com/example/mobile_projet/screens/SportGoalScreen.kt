package com.example.mobile_projet.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile_projet.R

// 运动类型枚举，包含单位信息和卡路里消耗
enum class SportType(
    val displayName: String, 
    val unit: String,
    val caloriesPerUnit: Double, // 每单位消耗的卡路里
    val iconRes: Int // 图标资源ID
) {
    RUNNING("Course à pied", "km", 60.0, R.drawable.ic_sport_running),        // 60 kcal/km
    CYCLING("Vélo", "km", 40.0, R.drawable.ic_sport_cycling),                 // 40 kcal/km
    SWIMMING("Natation", "m", 0.15, R.drawable.ic_sport_swimming),            // 0.15 kcal/m (150 kcal/1000m)
    WALKING("Marche", "km", 30.0, R.drawable.ic_sport_walking),               // 30 kcal/km
    YOGA("Yoga", "min", 3.0, R.drawable.ic_sport_yoga),                       // 3 kcal/min
    WEIGHT_TRAINING("Musculation", "min", 5.0, R.drawable.ic_sport_weight_training),  // 5 kcal/min
    PUSH_UPS("Pompes", "fois", 0.5, R.drawable.ic_sport_push_ups),            // 0.5 kcal/次
    SIT_UPS("Abdos", "fois", 0.3, R.drawable.ic_sport_sit_ups),               // 0.3 kcal/次
    PULL_UPS("Tractions", "fois", 1.0, R.drawable.ic_sport_pull_ups),         // 1 kcal/次
    JUMP_ROPE("Corde à sauter", "min", 10.0, R.drawable.ic_sport_jump_rope);  // 10 kcal/min (跳绳)
    
    companion object {
        fun fromDisplayName(name: String): SportType? {
            return values().find { it.displayName == name }
        }
        
        fun getUnitForSport(sportName: String): String {
            return fromDisplayName(sportName)?.unit ?: ""
        }
        
        fun getCaloriesForSport(sportName: String, amount: Double): Double {
            val sportType = fromDisplayName(sportName)
            return sportType?.let { it.caloriesPerUnit * amount } ?: 0.0
        }
        
        fun getIconForSport(sportName: String): Int {
            return fromDisplayName(sportName)?.iconRes ?: R.drawable.ic_exercise
        }
    }
}

data class SportGoal(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sportType: String,
    val goalLevel: String,
    val distance: String,
    val unit: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    // 计算该运动的卡路里消耗
    fun getCalories(): Double {
        val amount = distance.toDoubleOrNull() ?: 0.0
        return SportType.getCaloriesForSport(sportType, amount)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportGoalScreen(
    onDismiss: () -> Unit,
    onConfirm: (SportGoal) -> Unit,
    initialGoal: SportGoal? = null,
    existingSportGoals: List<SportGoal> = emptyList()
) {
    var selectedSport by remember { mutableStateOf(initialGoal?.sportType ?: "") }
    var expandedSportMenu by remember { mutableStateOf(false) }
    var selectedGoal by remember { 
        mutableStateOf(
            if (initialGoal != null && initialGoal.goalLevel != "Personnalisé") {
                initialGoal.distance
            } else {
                ""
            }
        )
    }
    var customDistance by remember { 
        mutableStateOf(
            if (initialGoal != null && initialGoal.goalLevel == "Personnalisé") {
                initialGoal.distance
            } else {
                ""
            }
        )
    }
    
    // 获取当前选择运动的单位
    val currentUnit = SportType.getUnitForSport(selectedSport)
    
    // 过滤掉已添加的运动类型（编辑模式除外）
    val sportTypes = if (initialGoal != null) {
        // 编辑模式：显示所有运动类型
        SportType.values().map { it.displayName }
    } else {
        // 新增模式：只显示未添加的运动类型
        val existingSportTypes = existingSportGoals.map { it.sportType }.toSet()
        SportType.values()
            .map { it.displayName }
            .filter { it !in existingSportTypes }
    }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 可滚动的内容区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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
                text = "Quel exercice avez-vous terminé aujourd'hui ?",
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
                // 根据运动类型动态设置目标值
                val (beginner, standard, challenge) = when (currentUnit) {
                    "km" -> Triple("1", "2", "5")
                    "m" -> Triple("100", "300", "500")
                    "min" -> Triple("10", "20", "30")
                    "fois" -> Triple("10", "20", "50")
                    "sec" -> Triple("30", "60", "120")
                    else -> Triple("1", "2", "5")
                }
                
                // Débutant
                GoalButton(
                    label = "Débutant",
                    distance = if (currentUnit.isNotEmpty()) "$beginner$currentUnit" else beginner,
                    isSelected = selectedGoal == beginner,
                    onClick = {
                        selectedGoal = beginner
                        customDistance = ""
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedSport.isNotEmpty()
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Standard
                GoalButton(
                    label = "Standard",
                    distance = if (currentUnit.isNotEmpty()) "$standard$currentUnit" else standard,
                    isSelected = selectedGoal == standard,
                    onClick = {
                        selectedGoal = standard
                        customDistance = ""
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedSport.isNotEmpty()
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Défi
                GoalButton(
                    label = "Défi",
                    distance = if (currentUnit.isNotEmpty()) "$challenge$currentUnit" else challenge,
                    isSelected = selectedGoal == challenge,
                    onClick = {
                        selectedGoal = challenge
                        customDistance = ""
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedSport.isNotEmpty()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 4. 自定义运动量输入框
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                        onValueChange = { newValue ->
                            // 只允许输入数字和小数点
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                customDistance = newValue
                                if (newValue.isNotEmpty()) {
                                    selectedGoal = ""
                                }
                            }
                        },
                        placeholder = { 
                            Text(
                                if (currentUnit.isNotEmpty()) {
                                    "Personnaliser ($currentUnit)"
                                } else {
                                    "Personnaliser"
                                }
                            ) 
                        },
                        suffix = {
                            if (currentUnit.isNotEmpty() && customDistance.isNotEmpty()) {
                                Text(
                                    text = currentUnit,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        },
                        enabled = selectedSport.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFFFB74D),
                            unfocusedContainerColor = Color(0xFFFFB74D),
                            disabledContainerColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFFFF9800),
                            unfocusedBorderColor = Color(0xFFFF9800),
                            disabledBorderColor = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true
                    )
                }
                
                // 提示文字
                Text(
                    text = "* Veuillez saisir uniquement des chiffres",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 60.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            }  // 结束可滚动内容区域
            
            // 5. 取消和确认按钮 - 固定在底部
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
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
                            // 根据运动类型判断目标等级
                            val (beginner, standard, challenge) = when (currentUnit) {
                                "km" -> Triple("1", "2", "5")
                                "m" -> Triple("100", "300", "500")
                                "min" -> Triple("10", "20", "30")
                                "fois" -> Triple("10", "20", "50")
                                "sec" -> Triple("30", "60", "120")
                                else -> Triple("1", "2", "5")
                            }
                            
                            val goalLevel = when (selectedGoal) {
                                beginner -> "Débutant"
                                standard -> "Standard"
                                challenge -> "Défi"
                                else -> "Personnalisé"
                            }
                            
                            onConfirm(SportGoal(
                                sportType = selectedSport, 
                                goalLevel = goalLevel, 
                                distance = finalDistance,
                                unit = currentUnit
                            ))
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
        }  // 结束外层 Column
    }  // 结束 Surface
}

@Composable
fun GoalButton(
    label: String,
    distance: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
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
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Color(0xFFFF9800) else Color(0xFFFFB74D),
                disabledContainerColor = Color(0xFFE0E0E0)
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

