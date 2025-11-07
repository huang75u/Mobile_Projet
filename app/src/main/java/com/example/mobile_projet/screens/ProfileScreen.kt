package com.example.mobile_projet.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.mobile_projet.R
import com.example.mobile_projet.data.ImageStorage
import com.example.mobile_projet.data.UserDataManager
import com.example.mobile_projet.data.UserPreferences
import androidx.navigation.NavController
import java.io.File
import com.example.mobile_projet.data.firebase.FriendsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun ProfileScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val userPrefs = remember { UserDataManager.getUserPreferences(context) }
    val scope = rememberCoroutineScope()
    val repo = remember { FriendsRepository() }
    
    var username by remember { mutableStateOf(userPrefs.username) }
    var points by remember { mutableStateOf(userPrefs.points) }
    var steps by remember { mutableStateOf(userPrefs.steps) }
    var isEditingUsername by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf(username) }
    
    // 权限相关状态
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }

    // 监听数据变化，实时更新
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        while (true) {
            points = userPrefs.points
            username = userPrefs.username
            steps = userPrefs.steps
            kotlinx.coroutines.delay(1000) // 每秒检查一次
        }
    }
    
    var backgroundImageFile by remember { mutableStateOf(ImageStorage.getBackgroundImageFile(context)) }
    var avatarImageFile by remember { mutableStateOf(ImageStorage.getAvatarImageFile(context)) }
    var backgroundImageKey by remember { mutableStateOf(0) }  // 用于强制刷新背景图片
    var avatarImageKey by remember { mutableStateOf(0) }  // 用于强制刷新头像
    
    var imagePickerType by remember { mutableStateOf<ImagePickerType?>(null) }
    
    // 背景图片选择器 - 必须在 permissionLauncher 之前声明
    val backgroundImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("ProfileScreen", "Background image picker result: $uri")
        uri?.let {
            if (ImageStorage.saveBackgroundImage(context, it)) {
                backgroundImageFile = ImageStorage.getBackgroundImageFile(context)
                backgroundImageKey++ // 强制刷新图片
                Log.d("ProfileScreen", "Background image saved successfully")
            } else {
                Log.e("ProfileScreen", "Failed to save background image")
            }
        }
    }
    
    // 头像选择器 - 必须在 permissionLauncher 之前声明
    val avatarImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("ProfileScreen", "Avatar image picker result: $uri")
        uri?.let {
            if (ImageStorage.saveAvatarImage(context, it)) {
                avatarImageFile = ImageStorage.getAvatarImageFile(context)
                avatarImageKey++ // 强制刷新图片
                Log.d("ProfileScreen", "Avatar image saved successfully")
                // 同步上传到 Firebase（从已保存的本地文件读取，避免部分URI读不到的问题）
                scope.launch {
                    try {
                        val uid = repo.signInAnonymouslyIfNeeded()
                        val bytes = avatarImageFile?.readBytes()
                        if (bytes != null && bytes.isNotEmpty()) {
                            repo.uploadAvatarAndSave(uid, bytes)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                Log.e("ProfileScreen", "Failed to save avatar image")
            }
        }
    }
    
    // 权限请求
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Log.d("ProfileScreen", "Permission result: isGranted=$isGranted, type=$imagePickerType")
        if (isGranted) {
            // 权限被授予，启动图片选择器
            when (imagePickerType) {
                ImagePickerType.BACKGROUND -> {
                    Log.d("ProfileScreen", "Launching background image picker")
                    backgroundImagePicker.launch("image/*")
                }
                ImagePickerType.AVATAR -> {
                    Log.d("ProfileScreen", "Launching avatar image picker")
                    avatarImagePicker.launch("image/*")
                }
                null -> {
                    Log.w("ProfileScreen", "imagePickerType is null after permission grant")
                }
            }
        } else {
            Log.w("ProfileScreen", "Permission denied - showing dialog")
            // 权限被拒绝，显示说明对话框
            showPermissionDeniedDialog = true
        }
        imagePickerType = null
    }
    
    // 请求权限的函数
    fun requestImagePermission(type: ImagePickerType) {
        Log.d("ProfileScreen", "requestImagePermission called with type: $type")
        imagePickerType = type
        
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        Log.d("ProfileScreen", "Checking permission: $permission, SDK: ${Build.VERSION.SDK_INT}")
        val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        Log.d("ProfileScreen", "Has permission: $hasPermission")
        
        if (hasPermission) {
            // 已有权限，直接启动选择器
            when (type) {
                ImagePickerType.BACKGROUND -> {
                    Log.d("ProfileScreen", "Launching background picker directly")
                    backgroundImagePicker.launch("image/*")
                }
                ImagePickerType.AVATAR -> {
                    Log.d("ProfileScreen", "Launching avatar picker directly")
                    avatarImagePicker.launch("image/*")
                }
            }
        } else {
            // 请求权限
            Log.d("ProfileScreen", "Requesting permission: $permission")
            permissionLauncher.launch(permission)
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 背景图片区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clickable { 
                    Log.d("ProfileScreen", "Background area clicked")
                    requestImagePermission(ImagePickerType.BACKGROUND) 
                }
        ) {
            if (backgroundImageFile != null) {
                key(backgroundImageKey) {  // 使用key强制重新加载整个组件
                    AsyncImage(
                        model = backgroundImageFile,
                        contentDescription = "背景图片",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // 默认背景 - 渐变色
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF87CEEB),
                                    Color(0xFFE0F7FA)
                                )
                            )
                        )
                )
                
                // 提示文字 - 只在没有背景图片时显示
                Text(
                    text = "点击更换背景",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // 编辑图标 - 始终显示
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑背景",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp),
                    tint = Color(0xFF007AFF)
                )
            }
        }
        
        // 主要内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .clickable { 
                        Log.d("ProfileScreen", "Avatar clicked")
                        requestImagePermission(ImagePickerType.AVATAR) 
                    }
            ) {
                if (avatarImageFile != null) {
                    key(avatarImageKey) {  // 使用key强制重新加载整个组件
                        AsyncImage(
                            model = avatarImageFile,
                            contentDescription = "头像",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // 默认头像
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF9C89CC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "默认头像",
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }
                }
                
                // 编辑图标
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomEnd),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑头像",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp),
                        tint = Color(0xFF007AFF)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 昵称编辑区域
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isEditingUsername) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = tempUsername,
                            onValueChange = { tempUsername = it },
                            modifier = Modifier.width(200.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                username = tempUsername
                                userPrefs.username = tempUsername
                                isEditingUsername = false
                                // 同步昵称到 Firebase
                                scope.launch {
                                    try {
                                        val uid = repo.signInAnonymouslyIfNeeded()
                                        repo.updateDisplayName(uid, tempUsername)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Enregistrer")
                        }
                    }
                } else {
                    // 昵称居中显示
                    Text(
                        text = username,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // 编辑按钮放在右侧，但不影响昵称居中
                    IconButton(
                        onClick = {
                            tempUsername = username
                            isEditingUsername = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "编辑昵称",
                            tint = Color(0xFF007AFF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 积分显示
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_points),
                    contentDescription = "积分",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = points.toString(),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 好友列表入口
            Button(
                onClick = { navController?.navigate("friends") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_friends),
                    contentDescription = "好友列表",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "List d'amis", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 步数卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF9E6)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 步数图标
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFFF8E5B4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_steps),
                            contentDescription = "步数",
                            modifier = Modifier.size(50.dp),
                            tint = Color.Unspecified
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Pas du jour",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = steps,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
    
    // 权限被拒绝的对话框
    if (showPermissionDeniedDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPermissionDeniedDialog = false 
                imagePickerType = null
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF007AFF)
                )
            },
            title = {
                Text(text = "需要存储权限", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "为了更换头像和背景图片，需要访问您的相册。\n\n" +
                            "请在手机设置中允许此应用访问照片权限：\n" +
                            "设置 → 应用 → Mobile_Projet → 权限 → 照片",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDeniedDialog = false
                        imagePickerType = null
                        // 打开应用设置页面
                        try {
                            val intent = android.content.Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Failed to open settings", e)
                        }
                    }
                ) {
                    Text("去设置")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showPermissionDeniedDialog = false 
                        imagePickerType = null
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }
}

enum class ImagePickerType {
    BACKGROUND,
    AVATAR
}
