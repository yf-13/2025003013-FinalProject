package com.example.studyflash.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object GroupDetail : Screen("group/{groupId}") {
        fun pass(groupId: Long) = "group/$groupId"
    }
    object StudyMode : Screen("study/{groupId}") {
        fun pass(groupId: Long) = "study/$groupId"
    }
    object AddGroup : Screen("add_group")
    object AddCard : Screen("add_card/{groupId}") {
        fun pass(groupId: Long) = "add_card/$groupId"
    }
    object Settings : Screen("settings")
}