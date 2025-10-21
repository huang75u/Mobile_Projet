package com.example.mobile_projet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile_projet.R
import com.example.mobile_projet.data.firebase.FriendActivity
import com.example.mobile_projet.data.firebase.FriendsRepository
import kotlinx.coroutines.launch

@Composable
fun FriendDetailsScreen(friendUid: String, onBack: () -> Unit) {
    val repo = remember { FriendsRepository() }
    var displayName by remember { mutableStateOf("") }
    var activities by remember { mutableStateOf<List<FriendActivity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(friendUid) {
        scope.launch {
            loading = true
            val data = repo.getFriendDetails(friendUid)
            if (data != null) {
                displayName = data.first.displayName
                activities = data.second
            }
            loading = false
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Image(painter = painterResource(id = R.drawable.retourne), contentDescription = "返回")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = displayName.ifBlank { "Détails" }, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(activities) { act ->
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = act.sportType, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(text = "${act.calories} kcal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            }
        }
    }
}


