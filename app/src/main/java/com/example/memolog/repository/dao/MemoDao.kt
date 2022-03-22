package com.example.memolog.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.memolog.repository.entity.Memo

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo ORDER BY id DESC")
    fun getAll(): LiveData<List<Memo>>

    @Insert()
    suspend fun insertMemo(memo: Memo) : Long // 해당 메모의 id를 알 수 있도록 // TODO :: Int 는 왜안돼?

    @Query("DELETE FROM memo")
    suspend fun deleteAll()
}