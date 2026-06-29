package com.example.studyflash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studyflash.navigation.Screen
import com.example.studyflash.ui.screens.*
import com.example.studyflash.ui.theme.StudyFlashTheme
import com.example.studyflash.viewmodel.StudyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StudyViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            StudyFlashTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToGroup = { groupId ->
                                    navController.navigate(Screen.GroupDetail.pass(groupId))
                                },
                                onNavigateToAddGroup = {
                                    navController.navigate(Screen.AddGroup.route)
                                },
                                onNavigateToSettings = {
                                    navController.navigate(Screen.Settings.route)
                                }
                            )
                        }

                        composable(Screen.AddGroup.route) {
                            AddGroupScreen(
                                viewModel = viewModel,
                                onGroupCreated = { groupId ->
                                    navController.popBackStack()
                                    navController.navigate(Screen.GroupDetail.pass(groupId))
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Screen.GroupDetail.route,
                            arguments = listOf(
                                navArgument("groupId") {
                                    defaultValue = 0L
                                }
                            )
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                            GroupDetailScreen(
                                viewModel = viewModel,
                                groupId = groupId,
                                onNavigateToAddCard = {
                                    navController.navigate(Screen.AddCard.pass(groupId))
                                },
                                onNavigateToStudy = {
                                    navController.navigate(Screen.StudyMode.pass(groupId))
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Screen.AddCard.route,
                            arguments = listOf(
                                navArgument("groupId") {
                                    defaultValue = 0L
                                }
                            )
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                            AddCardScreen(
                                viewModel = viewModel,
                                groupId = groupId,
                                onCardAdded = {
                                    navController.popBackStack()
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = Screen.StudyMode.route,
                            arguments = listOf(
                                navArgument("groupId") {
                                    defaultValue = 0L
                                }
                            )
                        ) { backStackEntry ->
                            val groupId = backStackEntry.arguments?.getLong("groupId") ?: 0L
                            StudyScreen(
                                viewModel = viewModel,
                                groupId = groupId,
                                onExit = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}