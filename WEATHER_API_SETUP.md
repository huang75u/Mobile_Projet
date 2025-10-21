# 天气功能设置指南

## 📋 概述

已成功集成 OpenWeatherMap API 来显示实时天气信息。

## 🔑 获取 API Key 步骤

### 1. 注册账号
访问 [OpenWeatherMap](https://openweathermap.org/) 并点击 "Sign Up" 创建免费账号。

### 2. 获取 API Key
1. 登录后，访问 [API Keys 页面](https://home.openweathermap.org/api_keys)
2. 复制默认的 API key（或创建新的）
3. **注意**：新创建的 API key 需要等待约 10-15 分钟才能激活

### 3. 配置 API Key
打开文件：`app/src/main/java/com/example/mobile_projet/data/weather/WeatherApiService.kt`

找到这一行：
```kotlin
const val API_KEY = "YOUR_API_KEY_HERE"
```

替换为你的实际 API Key：
```kotlin
const val API_KEY = "你的实际API密钥"
```

## 🌍 修改默认城市

打开文件：`app/src/main/java/com/example/mobile_projet/viewmodels/WeatherViewModel.kt`

找到 `init` 块：
```kotlin
init {
    // 默认加载巴黎的天气（可以改成其他城市）
    loadWeather("Paris")
}
```

修改城市名称（使用英文）：
```kotlin
init {
    loadWeather("Lyon")      // 里昂
    // 或
    loadWeather("Marseille") // 马赛
    // 或
    loadWeather("Beijing")   // 北京
}
```

## 📱 功能说明

### 显示的信息
- 🌡️ **温度**：当前温度（摄氏度）
- 🌤️ **天气状况**：晴天、多云、雨天等（带emoji图标）
- 💧 **湿度**：空气湿度百分比
- 💨 **风速**：当前风速（米/秒）
- 📍 **城市名称**：当前显示的城市

### 天气状态
- **加载中**：显示加载动画
- **成功**：显示完整天气信息
- **错误**：显示"Météo indisponible"（天气不可用）

## 🔧 高级功能

### 根据坐标获取天气
如果想使用GPS定位获取当前位置的天气：

```kotlin
// 在 WeatherViewModel 中调用
weatherViewModel.loadWeatherByLocation(
    latitude = 48.8566,  // 纬度
    longitude = 2.3522   // 经度
)
```

### 刷新天气
```kotlin
weatherViewModel.refreshWeather("Paris")
```

## 🚨 常见问题

### API Key 无效
- 确保已激活（等待 10-15 分钟）
- 检查是否正确复制（无多余空格）
- 确认账号已验证邮箱

### 网络错误
- 检查网络权限是否已添加到 `AndroidManifest.xml`
- 确认设备有网络连接
- 检查防火墙设置

### 城市名称找不到
- 使用英文城市名称
- 尝试使用大城市名称
- 可以使用坐标替代

## 📦 已添加的依赖

```kotlin
// Retrofit for network calls
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

## 🌐 API 限制

免费版 OpenWeatherMap 限制：
- ✅ 每分钟 60 次调用
- ✅ 每天 1,000,000 次调用
- ✅ 当前天气数据
- ❌ 小时预报（需付费）
- ❌ 历史数据（需付费）

对于正常使用完全足够！

## 📄 文件结构

```
app/src/main/java/com/example/mobile_projet/
├── data/
│   └── weather/
│       ├── WeatherModels.kt          # 数据模型
│       ├── WeatherApiService.kt      # API 接口定义
│       └── WeatherRepository.kt      # 数据仓库
├── viewmodels/
│   └── WeatherViewModel.kt           # 天气 ViewModel
└── screens/
    └── HomeScreen.kt                 # 主界面（已集成天气显示）
```

## 🎉 完成！

配置好 API Key 后，运行应用即可看到实时天气信息！

