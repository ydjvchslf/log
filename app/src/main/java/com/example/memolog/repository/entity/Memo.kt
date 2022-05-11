package com.example.memolog.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class Memo(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo var title: String,
    @ColumnInfo var content: String,
    @ColumnInfo var isFavorite: Boolean,
    @ColumnInfo var isLocked: Boolean,
    @ColumnInfo var password: String?,
    @ColumnInfo var isBookmark: Boolean,
    @ColumnInfo var createdTime: String,
    @ColumnInfo var updatedTime: String,
    @ColumnInfo var image: List<String>,
){
    override fun toString(): String {
        return "id: $id, isFavorite: $isFavorite, isLocked: $isLocked, password: $password, " +
                "image: $image"
    }
}