package com.example.memolog.feature.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.memolog.ViewModelFactory
import com.example.memolog.databinding.FragmentSearchBinding
import com.example.memolog.feature.detail.DetailViewModel
import com.example.memolog.repository.MemoRepository

class SearchFragment: Fragment(){
    private lateinit var binding: FragmentSearchBinding
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelBtn.setOnClickListener {
            Toast.makeText(binding.root.context, "취소버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViewModel() {
        viewModelFactory = ViewModelFactory(MemoRepository())
        searchViewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
    }
}