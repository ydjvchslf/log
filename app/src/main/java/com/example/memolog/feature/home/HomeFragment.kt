package com.example.memolog.feature.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memolog.ViewModelFactory
import com.example.memolog.adapter.RecyclerViewAdapter
import com.example.memolog.databinding.FragmentHomeBinding
import com.example.memolog.model.MemoModel
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo

class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeBinding
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var homeViewModel: HomeViewModel
    //lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val memoAdapter = RecyclerViewAdapter()

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = memoAdapter
            setHasFixedSize(true)
        }

        homeViewModel.memoList.observe(viewLifecycleOwner){
            Log.d("MemoDebug", "HomeFragment:: size : ${it.size}")
            val memoModel = it.map {
                MemoModel.fromEntity(it)
            }
            memoAdapter.setListData(memoModel as ArrayList<MemoModel>)
        }

        binding.deleteBtn.setOnClickListener {
            homeViewModel.deleteAll()
        }

    }

    private fun initViewModel(){
        viewModelFactory = ViewModelFactory(MemoRepository())
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }
}