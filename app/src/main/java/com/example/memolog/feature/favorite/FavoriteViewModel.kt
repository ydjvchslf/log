package com.example.memolog.feature.favorite


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(private val memoRepository: MemoRepository): ViewModel() {

    var memoList : LiveData<List<Memo>> = memoRepository.getAllFavorite(true)

    fun getAllFavoriteMemos(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("MemoDebug", "FavoriteViewModel::getAllFavoriteMemos-()")
            memoList = memoRepository.getAllFavorite(true)
        }
    }
}