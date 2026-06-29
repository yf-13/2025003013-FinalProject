package com.example.studyflash.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studyflash.data.dao.CardDao
import com.example.studyflash.data.dao.CardGroupDao
import com.example.studyflash.data.entity.CardEntity
import com.example.studyflash.data.entity.CardGroupEntity

@Database(
    entities = [CardGroupEntity::class, CardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardGroupDao(): CardGroupDao
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyflash_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}