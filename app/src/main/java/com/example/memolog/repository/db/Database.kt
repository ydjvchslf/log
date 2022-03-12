package com.example.memolog.repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.memolog.repository.entity.Memo
import com.example.memolog.repository.dao.MemoDao

@Database(entities = [Memo::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun memoDao(): MemoDao
}