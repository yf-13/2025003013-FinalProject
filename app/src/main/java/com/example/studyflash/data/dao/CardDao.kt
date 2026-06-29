package com.example.studyflash.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studyflash.data.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Query("DELETE FROM cards WHERE id = :cardId")
    suspend fun deleteCard(cardId: Long)

    @Query("DELETE FROM cards WHERE groupId = :groupId")
    suspend fun deleteCardsByGroup(groupId: Long)

    @Query("SELECT * FROM cards WHERE groupId = :groupId ORDER BY created_at DESC")
    fun getCardsByGroup(groupId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE groupId = :groupId AND mastered = 0")
    fun getUnmasteredCards(groupId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE front LIKE '%' || :keyword || '%' OR back LIKE '%' || :keyword || '%'")
    fun searchCards(keyword: String): Flow<List<CardEntity>>

    @Query("SELECT COUNT(*) FROM cards WHERE groupId = :groupId")
    suspend fun getCardCount(groupId: Long): Int

    @Query("SELECT COUNT(*) FROM cards WHERE groupId = :groupId AND mastered = 1")
    suspend fun getMasteredCount(groupId: Long): Int
}