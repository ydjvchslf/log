package com.example.memolog.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.memolog.repository.StringListTypeConverter
import com.example.memolog.repository.entity.Memo
import com.example.memolog.repository.dao.MemoDao

@Database(
    entities = [Memo::class],
    version = 2
)
@TypeConverters(
    value = [StringListTypeConverter::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
}