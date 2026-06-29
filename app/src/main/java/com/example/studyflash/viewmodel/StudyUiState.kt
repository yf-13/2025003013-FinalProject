package com.example.studyflash.viewmodel

import com.example.studyflash.data.entity.CardEntity
import com.example.studyflash.data.entity.CardGroupEntity

sealed interface GroupListUiState {
    object Loading : GroupListUiState
    object Empty : GroupListUiState
    data class Success(val groups: List<CardGroupEntity>) : GroupListUiState
    data class Error(val message: String) : GroupListUiState
}

sealed interface CardListUiState {
    object Loading : CardListUiState
    object Empty : CardListUiState
    data class Success(
        val cards: List<CardEntity>,
        val groupName: String = "",
        val progress: Float = 0f
    ) : CardListUiState
    data class Error(val message: String) : CardListUiState
}

sealed interface StudyModeUiState {
    object Loading : StudyModeUiState
    object Empty : StudyModeUiState
    data class Success(
        val cards: List<CardEntity>,
        val currentIndex: Int = 0,
        val isFlipped: Boolean = false
    ) : StudyModeUiState
    data class Error(val message: String) : StudyModeUiState
}