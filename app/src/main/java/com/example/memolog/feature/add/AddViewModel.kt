package com.example.memolog.feature.add

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddViewModel(private val memoRepository: MemoRepository): ViewModel() {

    private val memoId = MutableLiveData<Long>()

    fun insertMemo(memo: Memo) {
        CoroutineScope(Dispatchers.IO).launch {

            memoRepository.insertMemo(memo).let { id ->
                memoId.postValue(id)
            }
        }
    }

}