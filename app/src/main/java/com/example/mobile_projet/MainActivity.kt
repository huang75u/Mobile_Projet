package com.example.mobile_projet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.example.mobile_projet.data.UserDataManager
import com.example.mobile_projet.navigation.BottomNavigationBar
import com.example.mobile_projet.navigation.NavGraph
import com.example.mobile_projet.ui.theme.Mobile_ProjetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化 UserDataManager
        UserDataManager.init(this)
        enableEdgeToEdge()
        setContent {
            Mobile_ProjetTheme {
                MainScreen()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // 每次应用恢复时，触发日期检查
        // 这会在 ExerciseViewModel 的下一次访问时自动检查
        UserDataManager.init(this)
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // 监听应用生命周期，每次恢复时检查日期
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 应用恢复时，标记需要检查日期
                // ExerciseViewModel 会在下次访问时自动检查
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // 底部导航栏始终显示
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
