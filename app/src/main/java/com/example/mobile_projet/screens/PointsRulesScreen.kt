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
import com.example.mobile_projet.R

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
                            painter = painterResource(id = R.drawable.retourne),
                            contentDescription = "返回",
                            tint = Color.Unspecified,  // 保持原图颜色
                            modifier = Modifier.size(60.dp)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 左侧RULES图标
                Icon(
                    painter = painterResource(id = R.drawable.rules),
                    contentDescription = "Rules",
                    tint = Color.Unspecified,  // 保持原图颜色
                    modifier = Modifier.size(60.dp)
                )
                
                // 标题文本（居中）
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                
                // 占位（保持对称）
                Spacer(modifier = Modifier.size(60.dp))
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
                        text = "• Chaque exercice terminé : 1 point par 10 kcal brûlées",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exemple : Course 5km (300 kcal) = 30 points",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则2：目标完成度里程碑
                    Text(
                        text = "🎯 Règle 2 : Bonus selon l'objectif quotidien",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Atteindre 20% de l'objectif : +10 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Atteindre 40% de l'objectif : +20 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Atteindre 60% de l'objectif : +40 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Atteindre 80% de l'objectif : +60 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Atteindre 100% de l'objectif : +100 points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则3：超额完成奖励
                    Text(
                        text = "⭐ Règle 3 : Bonus de dépassement",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Dépasser 100% : +2 points par % supplémentaire",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Maximum : jusqu'à 150% (bonus max +100 points)",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则4：多样性奖励
                    Text(
                        text = "🔥 Règle 4 : Bonus de diversité",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6F00)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Faire 3 types d'exercices différents ou plus : +8 points par type",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exemple : Course, Yoga, Pompes = 3 × 8 = 24 points bonus",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 规则5：下限保护
                    Text(
                        text = "⚠️ Règle 5 : Seuil minimum",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF5350)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Vous devez atteindre au moins 20% de votre objectif quotidien pour gagner des points",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
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
                        text = "📌 Objectif quotidien : 500 kcal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Exercices terminés aujourd'hui :",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "• Course 3km (180 kcal) → 18 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Pompes 20× (10 kcal) → 1 point",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Yoga 30min (90 kcal) → 9 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Vélo 5km (200 kcal) → 20 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total calories : 480 kcal (96% de l'objectif)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
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
                        text = "• Points de base : 18 + 1 + 9 + 20 = 48 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Bonus 80% atteint : +60 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "• Bonus diversité (4 types) : +32 points",
                        fontSize = 12.sp,
                        color = Color.Black,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFF4CAF50), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "🎉 Total : 140 points gagnés aujourd'hui !",
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

