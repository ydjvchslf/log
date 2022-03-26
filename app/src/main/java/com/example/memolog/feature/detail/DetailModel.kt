package com.example.memolog.feature.detail

import android.util.Log
import androidx.lifecycle.*
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailModel(private val memoRepository: MemoRepository): ViewModel() {

    fun getOneMemo(id: Long, resultMemo: ((Memo) -> Unit)) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MemoDebug", "DetailModel::getOneMemo-()")
            val memo = memoRepository.selectOne(id)
            Log.d("MemoDebug", "memo: $memo")
            resultMemo.invoke(memo)
        }
    }

    fun updateMemo(memo: Memo){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MemoDebug", "DetailModel::updateMemo-()")
            memoRepository.updateMemo(memo)
        }
    }
}