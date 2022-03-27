package com.example.memolog.feature.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memolog.ViewModelFactory
import com.example.memolog.adapter.RecyclerViewAdapter
import com.example.memolog.databinding.FragmentFavoriteBinding
import com.example.memolog.model.MemoModel
import com.example.memolog.repository.MemoRepository


class FavoriteFragment : Fragment(){

    private lateinit var binding: FragmentFavoriteBinding
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        initViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MemoDebug", "FavoriteFragment:: onViewCreated-()")

//        val memoAdapter = RecyclerViewAdapter()
//
//        binding.recyclerView.apply {
//            layoutManager = GridLayoutManager(context, 2)
//            adapter = memoAdapter
//            setHasFixedSize(true)
//        }
//
//        favoriteViewModel.memoList.observe(viewLifecycleOwner){
//            Log.d("MemoDebug", "FavoriteFragment:: size : ${it.size}")
//            val memoModel = it.map {
//                MemoModel.fromEntity(it)
//            }
//            memoAdapter.setListData(memoModel as ArrayList<MemoModel>)
//        }
    }

    private fun initViewModel(){
        viewModelFactory = ViewModelFactory(MemoRepository())
        favoriteViewModel = ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel::class.java)
    }
}