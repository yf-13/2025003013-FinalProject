package com.example.studyflash.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.studyflash.data.entity.CardEntity
import com.example.studyflash.viewmodel.CardListUiState
import com.example.studyflash.viewmodel.StudyViewModel
import kotlinx.coroutines.launch
import kotlin.collections.filter
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GroupDetailScreen(
    viewModel: StudyViewModel,
    groupId: Long,
    onNavigateToAddCard: () -> Unit,
    onNavigateToStudy: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val cardState by viewModel.cardState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // 下拉刷新状态
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = {
            isLoading = true
            viewModel.loadCards(groupId)
        }
    )

    LaunchedEffect(cardState) {
        if (cardState !is CardListUiState.Loading) {
            isLoading = false
        }
    }


    LaunchedEffect(groupId) {
        viewModel.loadCards(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (cardState is CardListUiState.Success) {
                        Text((cardState as CardListUiState.Success).groupName)
                    } else {
                        Text("卡片列表")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (cardState is CardListUiState.Success) {
                        val cards = (cardState as CardListUiState.Success).cards
                        if (cards.isNotEmpty()) {
                            TextButton(onClick = onNavigateToStudy) {
                                Icon(Icons.Default.PlayArrow, null)
                                Text("学习")
                            }
                        }
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除卡片组")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddCard) {
                Icon(Icons.Default.Add, contentDescription = "添加卡片")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .pullRefresh(pullRefreshState)
        ) {
            when (cardState) {
                is CardListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CardListUiState.Empty -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("🃏", style = MaterialTheme.typography.displayLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("还没有卡片", style = MaterialTheme.typography.titleLarge)
                        Text("点击右下角 + 添加", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is CardListUiState.Success -> {
                    val state = cardState as CardListUiState.Success
                    Column {
                        LinearProgressIndicator(
                            progress = state.progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "学习进度: ${(state.progress * 100).toInt()}% (${state.cards.filter { it.mastered }.size}/${state.cards.size})",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.cards, key = { it.id }) { card ->
                                CardItem(
                                    card = card,
                                    onToggleMastered = {
                                        viewModel.viewModelScope.launch {
                                            viewModel.toggleMastered(card)
                                        }
                                    },
                                    onDelete = {
                                        viewModel.viewModelScope.launch {
                                            viewModel.deleteCard(card.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is CardListUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("⚠️", style = MaterialTheme.typography.displayLarge)
                        Text((cardState as CardListUiState.Error).message)
                        Button(onClick = { viewModel.loadCards(groupId) }) {
                            Text("重试")
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个卡片组及其所有卡片吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            viewModel.deleteGroup(groupId)
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun CardItem(
    card: CardEntity,
    onToggleMastered: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.front,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = card.back,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!card.example.isNullOrBlank()) {
                    Text(
                        text = "📌 ${card.example}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                IconButton(onClick = onToggleMastered) {
                    Icon(
                        if (card.mastered) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (card.mastered) "已掌握" else "未掌握",
                        tint = if (card.mastered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}