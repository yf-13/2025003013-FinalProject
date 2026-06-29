package com.example.studyflash.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.studyflash.viewmodel.StudyViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    viewModel: StudyViewModel,
    groupId: Long,
    onCardAdded: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var front by remember { mutableStateOf("") }
    var back by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isFetchingExample by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加卡片") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 正面（问题/单词）
            OutlinedTextField(
                value = front,
                onValueChange = { front = it; errorMsg = null },
                label = { Text("正面（问题/单词） *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMsg != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 背面（答案/释义）
            OutlinedTextField(
                value = back,
                onValueChange = { back = it },
                label = { Text("背面（答案/释义） *") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMsg != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 例句（可自动获取）
            OutlinedTextField(
                value = example,
                onValueChange = { example = it },
                label = { Text("例句（可选）") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (front.isNotBlank() && !isFetchingExample) {
                        IconButton(
                            onClick = {
                                println("===== 点击获取例句按钮，单词: $front =====")
                                isFetchingExample = true
                                viewModel.viewModelScope.launch {
                                    try {
                                        val result = viewModel.fetchExample(front)
                                        println("===== UI 收到结果: $result =====")
                                        result?.let { example = it }
                                    } catch (e: Exception) {
                                        println("===== UI 异常: ${e.message} =====")
                                        e.printStackTrace()
                                    }
                                    isFetchingExample = false
                                }
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "获取例句")
                        }
                    }
                    if (isFetchingExample) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                }
            )

            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (front.isBlank()) {
                        errorMsg = "请输入正面内容"
                        return@Button
                    }
                    if (back.isBlank()) {
                        errorMsg = "请输入背面内容"
                        return@Button
                    }
                    isLoading = true
                    viewModel.viewModelScope.launch {
                        try {
                            viewModel.createCard(groupId, front, back, example)
                            onCardAdded()
                        } catch (e: Exception) {
                            errorMsg = e.message ?: "创建失败"
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("添加卡片")
                }
            }
        }
    }
}