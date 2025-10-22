package com.example.mobile_projet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mobile_projet.R
import com.example.mobile_projet.viewmodels.LeaderboardUiState
import com.example.mobile_projet.viewmodels.LeaderboardViewModel

@Composable
fun LeaderboardScreen(onBack: () -> Unit, viewModel: LeaderboardViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Image(painter = painterResource(id = R.drawable.retourne), contentDescription = "back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Classement des\ncalories brûlées",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(painter = painterResource(id = R.drawable.ic_calories), contentDescription = null, modifier = Modifier.size(40.dp))
        }

        when (val s = state) {
            is LeaderboardUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is LeaderboardUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(s.message, color = Color.Red) }
            is LeaderboardUiState.Ready -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    s.entries.forEach { e ->
                        LeaderboardRow(
                            name = if (e.isMe) "Vous" else e.user.displayName.ifBlank { e.user.uid.take(6) },
                            avatarUrl = e.user.photoUrl,
                            calories = e.calories,
                            highlight = e.isMe
                        )
                        Divider(color = Color(0xFFFFCDD2))
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(name: String, avatarUrl: String?, calories: Int, highlight: Boolean) {
    val rowColor = if (highlight) Color(0xFFFFCDD2) else Color(0xFFFFEBEE)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // avatar
        Surface(shape = CircleShape, color = Color(0xFFE3F2FD)) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            } else {
                Image(painter = painterResource(id = R.drawable.ic_profile), contentDescription = null, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        // name chip
        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFCDD2)) {
            Text(text = name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "${calories}kcal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
    }
}


