package com.example.studyflash.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = CardGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "groupId")
    val groupId: Long,
    @ColumnInfo(name = "front")
    val front: String,
    @ColumnInfo(name = "back")
    val back: String,
    @ColumnInfo(name = "example")
    val example: String? = null,
    @ColumnInfo(name = "mastered")
    val mastered: Boolean = false,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)