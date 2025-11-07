package com.example.mobile_projet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mobile_projet.R
import com.example.mobile_projet.viewmodels.FriendsUiState
import com.example.mobile_projet.viewmodels.FriendsViewModel
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@Composable
fun FriendsScreen(
    onBack: () -> Unit,
    onShowAll: (friendUid: String) -> Unit,
    viewModel: FriendsViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部返回与标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Image(painter = painterResource(id = R.drawable.retourne), contentDescription = "返回")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ils s’entraînent à",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // 添加好友按钮卡片
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFD6EAF8),
                    modifier = Modifier.clickable { showAddDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加好友",
                        tint = Color(0xFF0D47A1),
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Ajouter des amis", fontSize = 18.sp)
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        when (val s = state) {
            is FriendsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is FriendsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.message, color = Color.Red)
                }
            }
            is FriendsUiState.Ready -> {
                val clipboard = LocalClipboardManager.current
                Column(modifier = Modifier.fillMaxSize()) {
                    // 我的信息卡片（头像、昵称、当日卡路里、UID复制）
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 头像
                            Surface(shape = CircleShape, color = Color(0xFFE8F0FE)) {
                                if (!s.myPhotoUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = s.myPhotoUrl,
                                        contentDescription = "me",
                                        modifier = Modifier.size(44.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(painter = painterResource(id = R.drawable.ic_profile), contentDescription = "me", modifier = Modifier.size(44.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = s.myDisplayName.ifBlank { "Pseudo" }, fontWeight = FontWeight.Bold)
                                Text(text = "Aujourd'hui: ${s.myTodayCalories} kcal", fontSize = 12.sp, color = Color.Gray)
                            }
                            // UID复制
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "UID", fontSize = 12.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = s.currentUid.take(10) + "...", color = Color(0xFF424242))
                                    IconButton(onClick = { clipboard.setText(AnnotatedString(s.currentUid)) }) {
                                        Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copier UID")
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    s.friends.forEach { preview ->
                        FriendRow(
                            avatarUrl = preview.user.photoUrl,
                            displayName = preview.user.displayName,
                            projectNames = preview.todayActivityNames,
                            hasMore = preview.hasMore,
                            onShowAll = { onShowAll(preview.user.uid) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddFriendDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { uid ->
                viewModel.addFriend(uid)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun FriendRow(
    avatarUrl: String?,
    displayName: String,
    projectNames: List<String>,
    hasMore: Boolean,
    onShowAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Surface(shape = CircleShape, shadowElevation = 0.dp, color = Color(0xFFE8F0FE)) {
            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "avatar",
                    modifier = Modifier.size(44.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "avatar",
                    modifier = Modifier.size(44.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        
        // 名字
        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFD6F5F0)) {
            Text(
                text = displayName,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        
        // 今日项目名（最多两个）
        projectNames.take(2).forEachIndexed { idx, name ->
            val display = if (idx == 1 && name.length > 8) "..." else name
            Surface(shape = RoundedCornerShape(10.dp), color = Color.White) {
                Text(
                    text = display,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .widthIn(min = 0.dp, max = if (idx == 0) 110.dp else 80.dp)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 查看全部
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFEEFB3),
            modifier = Modifier
                .widthIn(min = 36.dp)
                .clickable { onShowAll() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal =10.dp, vertical = 6.dp)) {
                Image(painter = painterResource(id = R.drawable.ic_eye), contentDescription = "show all", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun AddFriendDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var uid by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { if (uid.isNotBlank()) onConfirm(uid.trim()) }) {
                Text("Ajouter")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } },
        title = { Text("Ajouter un ami") },
        text = {
            Column {
                Text("Entrez l'UID de l'ami pour l'ajouter")
                OutlinedTextField(value = uid, onValueChange = { uid = it }, singleLine = true)
            }
        }
    )
}


