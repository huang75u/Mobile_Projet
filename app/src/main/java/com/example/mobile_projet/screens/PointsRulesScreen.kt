package com.example.mobile_projet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsRulesScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // 标题部分
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 左侧图标（绿色圆形美元图标）
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 标题文本
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Règles d'attribution",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "des points",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // 右侧RULES徽章
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF44336),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "RULES",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 第一部分：如何获得积分
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF9C4),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "*Comment obtenir des points",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则1：基础积分
                    Text(
                        text = "📊 Règle 1 : Points de base par calories",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Chaque activité complétée : 1 point par 10 kcal brûlées",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exemple : Compléter 5km de course (300 kcal) = 30 points",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则2：百分比里程碑
                    Text(
                        text = "🎯 Règle 2 : Bonus de progression quotidienne",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• 25% des objectifs complétés : +10 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• 50% des objectifs complétés : +30 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• 75% des objectifs complétés : +50 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• 100% des objectifs complétés : +100 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则3：全部完成奖励
                    Text(
                        text = "⭐ Règle 3 : Bonus de perfection",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Complétez TOUS vos objectifs du jour : +50 points supplémentaires",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则4：多项目组合
                    Text(
                        text = "🔥 Règle 4 : Bonus multi-activités",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Complétez 3 activités ou plus : +5 points par activité",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exemple : 5 activités complétées = 5 × 5 = 25 points bonus",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 第二部分：如何使用积分
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF9C4),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "*Comment utiliser les points",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Débloquez du contenu d'entraînement premium",
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Accédez à des plans d'entraînement personnalisés",
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Obtenez des récompenses exclusives",
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 计算示例
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💡 Exemple de calcul",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Objectifs du jour :",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "• Course 3km ✓ (180 kcal) → 18 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Pompes 20× ✓ (10 kcal) → 1 point",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Yoga 30min ✓ (90 kcal) → 9 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Natation 500m ✗ (75 kcal) → non complété",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF81C784))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Calcul des points :",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "• Points de base : 18 + 1 + 9 = 28 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Progression 75% : +50 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Multi-activités (3) : +15 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF4CAF50), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "🎉 Total : 93 points gagnés aujourd'hui !",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 说明文字
            Text(
                text = "*Les points sont liés aux objectifs fixés, ainsi qu'au nombre maximal et minimal pouvant être obtenus par jour pour une même activité.",
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Les points accumulés peuvent être utilisés pour débloquer des fonctionnalités avancées, du contenu exclusif et des récompenses spéciales dans l'application.",
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Continuez à vous entraîner régulièrement pour maximiser vos gains de points et atteindre vos objectifs de fitness plus rapidement !",
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

