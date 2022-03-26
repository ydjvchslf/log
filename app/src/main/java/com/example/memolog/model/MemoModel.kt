package com.example.memolog.model

import com.example.memolog.repository.entity.Memo

data class MemoModel(
    val id: Long,
    val title: String,
    val content: String,
    val isFavorite: Boolean,
    val isLocked: Boolean,
    val password: String?,
    val isBookmark: Boolean
){
    companion object {
        fun fromEntity(entity: Memo) = MemoModel(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            isFavorite = entity.isFavorite,
            password = entity.password,
            isLocked = entity.isLocked,
            isBookmark = entity.isBookmark
        )
    }
}