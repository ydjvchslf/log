package com.example.memolog.feature.favorite


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo

class FavoriteViewModel(private val memoRepository: MemoRepository): ViewModel() {

    var memoList : LiveData<List<Memo>> = memoRepository.getAllFavorite(true)

}