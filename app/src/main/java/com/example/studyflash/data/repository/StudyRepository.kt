package com.example.studyflash.data.repository

import com.example.studyflash.data.dao.CardDao
import com.example.studyflash.data.dao.CardGroupDao
import com.example.studyflash.data.entity.CardEntity
import com.example.studyflash.data.entity.CardGroupEntity
import com.example.studyflash.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow

class StudyRepository(
    private val cardGroupDao: CardGroupDao,
    private val cardDao: CardDao
) {
    // === 网络 ===
    suspend fun fetchWordDefinition(word: String): String? {
        return try {
            println("===== 1. 开始请求 API，单词: $word =====")
            val response = RetrofitClient.apiService.getWordDefinition(word)
            println("===== 2. API 返回数据: $response =====")

            // 先尝试从 noun 中取，如果没有则从 verb 中取
            val meaning = response.firstOrNull()?.meaning
            var result: String? = null

            // 从 noun 中找
            meaning?.noun?.firstOrNull()?.let { def ->
                result = def.example?.takeIf { it.isNotBlank() }
                    ?: def.definition?.takeIf { it.isNotBlank() }
            }

            // 如果 noun 没有，从 verb 中找
            if (result == null) {
                meaning?.verb?.firstOrNull()?.let { def ->
                    result = def.example?.takeIf { it.isNotBlank() }
                        ?: def.definition?.takeIf { it.isNotBlank() }
                }
            }

            println("===== 3. 提取的结果: $result =====")
            result
        } catch (e: retrofit2.HttpException) {
            println("===== API 返回错误码: ${e.code()} =====")
            null
        } catch (e: Exception) {
            println("===== 网络请求失败: ${e.message} =====")
            e.printStackTrace()
            null
        }
    }

    // === 卡片组 CRUD ===
    suspend fun insertGroup(group: CardGroupEntity): Long = cardGroupDao.insertGroup(group)
    suspend fun updateGroup(group: CardGroupEntity) = cardGroupDao.updateGroup(group)
    suspend fun deleteGroup(groupId: Long) = cardGroupDao.deleteGroup(groupId)
    fun getAllGroups(): Flow<List<CardGroupEntity>> = cardGroupDao.getAllGroups()
    fun searchGroups(keyword: String): Flow<List<CardGroupEntity>> = cardGroupDao.searchGroups(keyword)

    // === 卡片 CRUD ===
    suspend fun insertCard(card: CardEntity): Long = cardDao.insertCard(card)
    suspend fun updateCard(card: CardEntity) = cardDao.updateCard(card)
    suspend fun deleteCard(cardId: Long) = cardDao.deleteCard(cardId)
    suspend fun deleteCardsByGroup(groupId: Long) = cardDao.deleteCardsByGroup(groupId)
    fun getCardsByGroup(groupId: Long): Flow<List<CardEntity>> = cardDao.getCardsByGroup(groupId)
    fun getUnmasteredCards(groupId: Long): Flow<List<CardEntity>> = cardDao.getUnmasteredCards(groupId)
    fun searchCards(keyword: String): Flow<List<CardEntity>> = cardDao.searchCards(keyword)

    // === 统计 ===
    suspend fun getCardCount(groupId: Long): Int = cardDao.getCardCount(groupId)
    suspend fun getMasteredCount(groupId: Long): Int = cardDao.getMasteredCount(groupId)

    // === 进度计算 ===
    suspend fun getProgress(groupId: Long): Float {
        val total = getCardCount(groupId)
        if (total == 0) return 0f
        val mastered = getMasteredCount(groupId)
        return mastered.toFloat() / total
    }
}