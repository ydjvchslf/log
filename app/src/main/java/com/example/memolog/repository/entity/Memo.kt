package com.example.memolog.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo var title: String,
    @ColumnInfo var content: String
)