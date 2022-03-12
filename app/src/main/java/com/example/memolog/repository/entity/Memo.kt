package com.example.memolog.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val title: String?,
    @ColumnInfo val content: String?,
    @ColumnInfo val favorite: Boolean,
    @ColumnInfo val password: String?,
    @ColumnInfo(name = "created_time") val createdTime: String?,
    @ColumnInfo(name = "designed_time") val designedTime: String?,
)