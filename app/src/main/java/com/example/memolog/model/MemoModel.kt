package com.example.memolog.model

import com.example.memolog.repository.entity.Memo

data class MemoModel(
    val id: Long,
    val title: String,
    val content: String
){
    companion object {
        fun fromEntity(entity: Memo) = MemoModel(
            id = entity.id,
            title = entity.title,
            content = entity.content
        )
    }
}