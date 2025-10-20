# 如何添加运动图标

本指南说明如何在运动卡片中添加自定义图标。

## 📍 图标位置

运动卡片的图标占位符位于：
`app/src/main/java/com/example/mobile_projet/screens/ExerciseScreen.kt`

第 188-193 行：
```kotlin
// TODO: 在这里添加运动图标
// Icon(painter = painterResource(id = R.drawable.icon_name), ...)
Text(
    text = "🏃", // 临时占位符
    fontSize = 32.sp
)
```

## 🎨 添加图标的步骤

### 方案 1: 使用图片资源（推荐）

1. **准备图标文件**
   - 支持格式：PNG, SVG, WebP
   - 建议尺寸：60x60dp 或更大
   - 建议背景：透明

2. **添加图标到项目**
   - 将图标文件放入 `app/src/main/res/drawable/` 目录
   - 文件命名规则：小写字母+下划线，例如：
     - `icon_running.png`
     - `icon_push_ups.png`
     - `icon_swimming.png`

3. **在代码中使用图标**

修改 `SportGoalCard` 中的图标部分：

```kotlin
// 图标占位符 - 根据运动类型显示不同图标
Box(
    modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .background(Color(0x33000000)),
    contentAlignment = Alignment.Center
) {
    // 根据运动类型选择图标
    val iconRes = when (goal.sportType) {
        "Course à pied" -> R.drawable.icon_running
        "Vélo" -> R.drawable.icon_cycling
        "Natation" -> R.drawable.icon_swimming
        "Marche" -> R.drawable.icon_walking
        "Yoga" -> R.drawable.icon_yoga
        "Musculation" -> R.drawable.icon_weight_training
        "Pompes" -> R.drawable.icon_push_ups
        "Abdos" -> R.drawable.icon_sit_ups
        "Tractions" -> R.drawable.icon_pull_ups
        "Planche" -> R.drawable.icon_plank
        else -> R.drawable.icon_default
    }
    
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = goal.sportType,
        modifier = Modifier.size(40.dp),
        tint = Color.White  // 可以根据需要调整颜色
    )
}
```

### 方案 2: 使用 Material Icons

如果不想添加自定义图片，可以使用 Material Icons：

```kotlin
// 图标占位符
Box(
    modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .background(Color(0x33000000)),
    contentAlignment = Alignment.Center
) {
    val icon = when (goal.sportType) {
        "Course à pied" -> Icons.Default.DirectionsRun
        "Vélo" -> Icons.Default.DirectionsBike
        "Natation" -> Icons.Default.Pool
        "Marche" -> Icons.Default.DirectionsWalk
        "Yoga" -> Icons.Default.SelfImprovement
        "Musculation" -> Icons.Default.FitnessCenter
        else -> Icons.Default.Sports
    }
    
    Icon(
        imageVector = icon,
        contentDescription = goal.sportType,
        modifier = Modifier.size(40.dp),
        tint = Color.White
    )
}
```

### 方案 3: 保留 Emoji（临时方案）

当前使用的是 emoji，你可以根据运动类型显示不同的 emoji：

```kotlin
Box(
    modifier = Modifier
        .size(60.dp)
        .clip(CircleShape)
        .background(Color(0x33000000)),
    contentAlignment = Alignment.Center
) {
    val emoji = when (goal.sportType) {
        "Course à pied" -> "🏃"
        "Vélo" -> "🚴"
        "Natation" -> "🏊"
        "Marche" -> "🚶"
        "Yoga" -> "🧘"
        "Musculation" -> "🏋️"
        "Pompes" -> "💪"
        "Abdos" -> "🤸"
        "Tractions" -> "🤾"
        "Planche" -> "🧍"
        else -> "⚡"
    }
    
    Text(
        text = emoji,
        fontSize = 32.sp
    )
}
```

## 🎯 推荐的图标风格

- **线条粗细**：2-3dp
- **颜色**：白色或深灰色
- **风格**：简约、扁平化
- **保持一致**：所有图标使用相同的风格

## 📦 图标资源推荐

免费图标资源网站：
- [Flaticon](https://www.flaticon.com/) - 大量运动图标
- [Icons8](https://icons8.com/) - 多种风格
- [Material Design Icons](https://fonts.google.com/icons) - Google官方图标

## 💡 注意事项

1. 确保图标版权允许使用
2. 优化图标大小以提高性能
3. 测试图标在不同设备上的显示效果
4. 保持所有图标风格统一

## 🔧 需要导入的库（如果使用图片资源）

在文件顶部添加：
```kotlin
import androidx.compose.ui.res.painterResource
```

已经在当前代码中导入了基本的图标库，如果需要更多 Material Icons，可能需要添加额外依赖。

