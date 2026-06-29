package com.example.studyflash.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyflash.data.database.AppDatabase
import com.example.studyflash.data.entity.CardEntity
import com.example.studyflash.data.entity.CardGroupEntity
import com.example.studyflash.data.repository.StudyRepository
import com.example.studyflash.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = StudyRepository(db.cardGroupDao(), db.cardDao())
    private val preferencesRepo = UserPreferencesRepository(application)

    // === 主题 ===
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // === 卡片组列表 ===
    private val _groupState = MutableStateFlow<GroupListUiState>(GroupListUiState.Loading)
    val groupState: StateFlow<GroupListUiState> = _groupState.asStateFlow()

    // === 卡片列表 ===
    private val _cardState = MutableStateFlow<CardListUiState>(CardListUiState.Loading)
    val cardState: StateFlow<CardListUiState> = _cardState.asStateFlow()

    // === 学习模式 ===
    private val _studyState = MutableStateFlow<StudyModeUiState>(StudyModeUiState.Loading)
    val studyState: StateFlow<StudyModeUiState> = _studyState.asStateFlow()

    // === 当前选中的卡片组 ===
    private val _currentGroupId = MutableStateFlow<Long?>(null)
    val currentGroupId: StateFlow<Long?> = _currentGroupId.asStateFlow()

    init {
        loadTheme()
        loadGroups()
        loadLastGroup()
    }

    private fun loadTheme() {
        viewModelScope.launch {
            preferencesRepo.themeFlow.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
    }

    private fun loadLastGroup() {
        viewModelScope.launch {
            preferencesRepo.lastGroupIdFlow.collect { groupId ->
                if (groupId != 0L) {
                    _currentGroupId.value = groupId
                }
            }
        }
    }

    // === 加载卡片组 ===
    fun loadGroups() {
        viewModelScope.launch {
            _groupState.value = GroupListUiState.Loading
            try {
                repository.getAllGroups().collect { groups ->
                    _groupState.value = if (groups.isEmpty()) {
                        GroupListUiState.Empty
                    } else {
                        GroupListUiState.Success(groups)
                    }
                }
            } catch (e: Exception) {
                _groupState.value = GroupListUiState.Error(e.message ?: "加载失败")
            }
        }
    }
    fun getCardCountForGroup(groupId: Long): StateFlow<Int> {
        val count = MutableStateFlow(0)
        viewModelScope.launch {
            count.value = repository.getCardCount(groupId)
        }
        return count.asStateFlow()
    }

    // === 加载卡片（按组） ===
    fun loadCards(groupId: Long) {
        viewModelScope.launch {
            _currentGroupId.value = groupId
            preferencesRepo.saveLastGroupId(groupId)
            _cardState.value = CardListUiState.Loading

            try {
                val progress = repository.getProgress(groupId)
                val group = repository.getAllGroups().first()
                    .find { it.id == groupId }

                repository.getCardsByGroup(groupId).collect { cards ->
                    _cardState.value = if (cards.isEmpty()) {
                        CardListUiState.Empty
                    } else {
                        CardListUiState.Success(
                            cards = cards,
                            groupName = group?.name ?: "",
                            progress = progress
                        )
                    }
                }
            } catch (e: Exception) {
                _cardState.value = CardListUiState.Error(e.message ?: "加载失败")
            }
        }
    }

    // === 创建卡片组 ===
    suspend fun createGroup(name: String, description: String? = null): Long {
        val group = CardGroupEntity(
            name = name,
            description = description
        )
        return repository.insertGroup(group)
    }

    // === 删除卡片组 ===
    suspend fun deleteGroup(groupId: Long) {
        repository.deleteGroup(groupId)
        if (_currentGroupId.value == groupId) {
            _currentGroupId.value = null
        }
        loadGroups()
    }

    // === 创建卡片 ===
    suspend fun createCard(groupId: Long, front: String, back: String, example: String? = null): Long {
        val card = CardEntity(
            groupId = groupId,
            front = front,
            back = back,
            example = example
        )
        return repository.insertCard(card)
    }

    // === 删除卡片 ===
    suspend fun deleteCard(cardId: Long) {
        repository.deleteCard(cardId)
        _currentGroupId.value?.let { loadCards(it) }
    }

    // === 切换掌握状态 ===
    suspend fun toggleMastered(card: CardEntity) {
        val updated = card.copy(mastered = !card.mastered)
        repository.updateCard(updated)
        _currentGroupId.value?.let { loadCards(it) }
    }

    // === 学习模式 ===
    fun enterStudyMode(groupId: Long) {
        viewModelScope.launch {
            _studyState.value = StudyModeUiState.Loading
            try {
                val cards = repository.getUnmasteredCards(groupId).first()
                _studyState.value = if (cards.isEmpty()) {
                    StudyModeUiState.Empty
                } else {
                    StudyModeUiState.Success(
                        cards = cards,
                        currentIndex = 0,
                        isFlipped = false
                    )
                }
            } catch (e: Exception) {
                _studyState.value = StudyModeUiState.Error(e.message ?: "加载失败")
            }
        }
    }

    fun flipCard() {
        val current = _studyState.value
        if (current is StudyModeUiState.Success) {
            _studyState.value = current.copy(isFlipped = !current.isFlipped)
        }
    }

    fun nextCard() {
        val current = _studyState.value
        if (current is StudyModeUiState.Success) {
            val nextIndex = current.currentIndex + 1
            if (nextIndex < current.cards.size) {
                _studyState.value = current.copy(
                    currentIndex = nextIndex,
                    isFlipped = false
                )
            } else {
                _currentGroupId.value?.let { loadCards(it) }
                _studyState.value = StudyModeUiState.Empty
            }
        }
    }

    fun exitStudyMode() {
        _studyState.value = StudyModeUiState.Loading
        _currentGroupId.value?.let { loadCards(it) }
    }

    // === 网络获取例句 ===
    suspend fun fetchExample(word: String): String? {
        return try {
            println("===== 4. ViewModel 开始调用 Repository =====")
            val result = repository.fetchWordDefinition(word)
            println("===== 5. ViewModel 收到结果: $result =====")
            result
        } catch (e: Exception) {
            println("===== 6. ViewModel 异常: ${e.message} =====")
            e.printStackTrace()
            null
        }
    }

    // === 搜索 ===
    fun searchGroups(keyword: String) {
        if (keyword.isBlank()) {
            loadGroups()
            return
        }
        viewModelScope.launch {
            repository.searchGroups(keyword).collect { groups ->
                _groupState.value = if (groups.isEmpty()) {
                    GroupListUiState.Empty
                } else {
                    GroupListUiState.Success(groups)
                }
            }
        }
    }

    // === 切换主题 ===
    fun toggleTheme() {
        viewModelScope.launch {
            val current = _isDarkTheme.value
            preferencesRepo.saveTheme(!current)
            _isDarkTheme.value = !current
        }
    }
}