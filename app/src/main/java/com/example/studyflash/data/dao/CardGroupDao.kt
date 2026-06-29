package com.example.studyflash.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studyflash.data.entity.CardGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: CardGroupEntity): Long

    @Update
    suspend fun updateGroup(group: CardGroupEntity)

    @Query("DELETE FROM card_groups WHERE id = :groupId")
    suspend fun deleteGroup(groupId: Long)

    @Query("SELECT * FROM card_groups ORDER BY created_at DESC")
    fun getAllGroups(): Flow<List<CardGroupEntity>>

    @Query("SELECT * FROM card_groups WHERE name LIKE '%' || :keyword || '%'")
    fun searchGroups(keyword: String): Flow<List<CardGroupEntity>>
}