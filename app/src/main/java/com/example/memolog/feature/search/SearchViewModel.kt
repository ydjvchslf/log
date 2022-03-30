package com.example.memolog.feature.search


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(private val memoRepository: MemoRepository): ViewModel() {

    var memoList : LiveData<List<Memo>> = memoRepository.getAllMemo()

    fun getAllMemos(){
        CoroutineScope(Dispatchers.IO).launch {

        }
    }

}