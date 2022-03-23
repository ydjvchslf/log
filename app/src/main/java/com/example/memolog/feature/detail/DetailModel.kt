package com.example.memolog.feature.add

import android.util.Log
import androidx.lifecycle.*
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddViewModel(private val memoRepository: MemoRepository): ViewModel() {

    var memoList : LiveData<List<Memo>> = memoRepository.getAllMemo()

    fun insertMemo(memo: Memo) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MemoDebug", "AddViewModel::insertMemo-()")
            memoRepository.insertMemo(memo).let { id ->
                //memoId.postValue(id) //setValue 메인쓰레드 반영, postValue는 백그라운드에서 반영
                Log.d("MemoDebug", "memoId: $id")
            }
        }
    }

    fun getAllMemo(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MemoDebug", "AddViewModel::getAllMemo-()")
            //memoList = memoRepository.getAllMemo()
        }
    }
}