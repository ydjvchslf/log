package com.example.memolog.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.memolog.repository.entity.Memo

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo ORDER BY id DESC")
    fun getAll(): LiveData<List<Memo>>

    @Query("SELECT * FROM memo WHERE isFavorite = :isFavorite")
    fun getAllFavorite(isFavorite: Boolean): LiveData<List<Memo>>

    @Insert()
    suspend fun insertMemo(memo: Memo) : Long // 해당 메모의 id를 알 수 있도록 // TODO :: Int 는 왜안돼?

    @Query("DELETE FROM memo")
    suspend fun deleteAll()

    @Query("DELETE FROM memo WHERE id = :id")
    suspend fun deleteOne(id: Long)

    @Query("SELECT * FROM memo WHERE id = :id")
    suspend fun selectOne(id: Long): Memo

    @Update
    suspend fun updateMemo(memo: Memo): Int

    @Query("UPDATE memo SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE memo SET password = :pw, isLocked = :isLock WHERE id = :id")
    suspend fun updateLock(id: Long, pw: String, isLock: Boolean)
}