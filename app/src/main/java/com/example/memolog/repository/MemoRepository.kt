package com.example.memolog.repository

import com.example.memolog.GlobalApplication
import com.example.memolog.repository.entity.Memo

class MemoRepository {
    private val appDBInstant = GlobalApplication.databaseInstance.memoDao()

    suspend fun insertMemo(memo: Memo) = appDBInstant.insertMemo(memo)
    suspend fun deleteMemo() = appDBInstant.deleteAll()
    suspend fun selectOne(id: Long) = appDBInstant.selectOne(id)
    suspend fun updateMemo(memo: Memo) = appDBInstant.updateMemo(memo)
    suspend fun setFavorite(id: Long, state: Boolean) = appDBInstant.updateFavorite(id, state)
    fun getAllMemo() = appDBInstant.getAll()
}