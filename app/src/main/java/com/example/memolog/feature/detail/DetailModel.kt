package com.example.memolog.feature.detail

import android.util.Log
import androidx.lifecycle.*
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailModel(private val memoRepository: MemoRepository): ViewModel() {

    init {

    }

    fun getOneMemo(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MemoDebug", "DetailModel::getOneMemo-()")

        }
    }
}