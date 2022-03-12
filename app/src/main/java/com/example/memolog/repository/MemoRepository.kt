package com.example.memolog.repository

import com.example.memolog.GlobalApplication
import com.example.memolog.repository.entity.Memo

class MemoRepository {
    private val appDBInstant = GlobalApplication.databaseInstance.memoDao()

    suspend fun insertMemo(memo: Memo) = appDBInstant.insertMemo(memo)
    suspend fun deleteMemo() = appDBInstant.deleteAll()
    suspend fun getAllMemo() = appDBInstant.getAll()
}