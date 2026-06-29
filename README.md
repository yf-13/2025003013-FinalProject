# 📚 学习卡片（StudyFlash）

一个基于 Jetpack Compose 构建的 Android 学习卡片应用，帮助用户通过卡片方式高效记忆知识。

---

## 📱 应用简介

**学习卡片（StudyFlash）** 是一款内容管理类工具应用，专为**学生、语言学习者、备考人员**设计。用户可以通过创建卡片组、添加卡片、学习模式等方式，高效地进行知识记忆和复习。

---

## ✨ 核心功能

| 功能 | 说明 |
|---|---|
| 📂 **卡片组管理** | 创建、删除卡片组，每个卡片组可包含多张卡片 |
| 🃏 **卡片管理** | 添加、删除卡片，卡片包含正面（问题）和背面（答案） |
| 📖 **学习模式** | 卡片翻转学习，点击卡片查看答案，标记掌握状态 |
| 📊 **进度追踪** | 实时显示学习进度（已掌握/总数） |
| 🔍 **搜索功能** | 按卡片组名称搜索 |
| 🌙 **深色模式** | 支持深色/浅色模式切换，自动保存偏好 |
| 🌐 **网络获取例句** | 集成 Free Dictionary API，自动获取单词例句 |
| 🔄 **下拉刷新** | 首页和详情页支持下拉刷新 |

---

## 🛠️ 技术栈

| 类别 | 技术 |
|---|---|
| 语言 | Kotlin |
| UI 框架 | Jetpack Compose + Material 3 |
| 架构模式 | MVVM（ViewModel + Repository） |
| 数据库 | Room（2张表） |
| 偏好存储 | DataStore |
| 网络请求 | Retrofit + Moshi |
| 异步处理 | Kotlin Coroutines + Flow |
| 导航 | Navigation Compose |
| 最低 SDK | API 24（Android 7.0） |
| 目标 SDK | API 34（Android 14） |

---

## 项目结构
\`\`\`
app/src/main/java/com/example/studyflash/
├── MainActivity.kt
├── data/
│   ├── entity/
│   │   ├── CardGroupEntity.kt
│   │   └── CardEntity.kt
│   ├── dao/
│   │   ├── CardGroupDao.kt
│   │   └── CardDao.kt
│   ├── database/
│   │   └── AppDatabase.kt
│   ├── network/
│   │   ├── ApiService.kt
│   │   ├── RetrofitClient.kt
│   │   └── dto/
│   │       └── DictionaryDto.kt
│   └── repository/
│       └── StudyRepository.kt
├── datastore/
│   └── UserPreferencesRepository.kt
├── navigation/
│   └── Screen.kt
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── GroupDetailScreen.kt
│   │   ├── StudyScreen.kt
│   │   ├── AddGroupScreen.kt
│   │   ├── AddCardScreen.kt
│   │   └── SettingsScreen.kt
│   ├── components/
│   └── theme/
│       ├── Color.kt
│       └── Theme.kt
└── viewmodel/
    ├── StudyViewModel.kt
    └── StudyUiState.kt
\`\`\`
---

## 🚀 运行说明

### 环境要求

- Android Studio Ladybug 或更新版本
- JDK 17+
- Android SDK API 24+
