package com.example.memolog.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.memolog.repository.entity.Memo

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo")
    fun getAll(): List<Memo>

    @Insert
    fun insertMemo(memo: Memo)

    @Query("DELETE FROM memo")
    fun deleteAll()
}