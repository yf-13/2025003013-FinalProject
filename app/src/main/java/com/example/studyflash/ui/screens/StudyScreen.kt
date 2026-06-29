package com.example.studyflash.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.studyflash.data.entity.CardEntity
import com.example.studyflash.viewmodel.StudyModeUiState
import com.example.studyflash.viewmodel.StudyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    viewModel: StudyViewModel,
    groupId: Long,
    onExit: () -> Unit
) {
    val studyState by viewModel.studyState.collectAsStateWithLifecycle()

    LaunchedEffect(groupId) {
        viewModel.enterStudyMode(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📖 学习模式") },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "退出")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (studyState) {
                is StudyModeUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("加载卡片...")
                    }
                }
                is StudyModeUiState.Empty -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("全部掌握！", style = MaterialTheme.typography.titleLarge)
                        Text("所有卡片已学完", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onExit) {
                            Text("返回")
                        }
                    }
                }
                is StudyModeUiState.Success -> {
                    val state = studyState as StudyModeUiState.Success
                    val currentCard = state.cards[state.currentIndex]

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "${state.currentIndex + 1} / ${state.cards.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clickable { viewModel.flipCard() },
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Text(
                                        text = if (state.isFlipped) currentCard.back else currentCard.front,
                                        style = if (state.isFlipped)
                                            MaterialTheme.typography.titleLarge
                                        else
                                            MaterialTheme.typography.headlineSmall,
                                        textAlign = TextAlign.Center
                                    )

                                    // 显示例句（如果有）
                                    if (state.isFlipped && !currentCard.example.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Divider()
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "📌 ${currentCard.example}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    if (!state.isFlipped) {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "👆 点击翻转",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (state.isFlipped) {
                                Button(
                                    onClick = {
                                        viewModel.viewModelScope.launch {
                                            viewModel.toggleMastered(currentCard)
                                            viewModel.nextCard()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Text("已掌握")
                                }
                            }

                            Button(
                                onClick = { viewModel.nextCard() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    if (state.currentIndex < state.cards.size - 1)
                                        "下一张"
                                    else
                                        "完成"
                                )
                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                is StudyModeUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚠️", style = MaterialTheme.typography.displayLarge)
                        Text((studyState as StudyModeUiState.Error).message)
                        Button(onClick = { viewModel.enterStudyMode(groupId) }) {
                            Text("重试")
                        }
                    }
                }
            }
        }
    }
}