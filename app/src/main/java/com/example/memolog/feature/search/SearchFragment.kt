package com.example.memolog.feature.search

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.memolog.R
import com.example.memolog.ViewModelFactory
import com.example.memolog.databinding.FragmentSearchBinding
import com.example.memolog.feature.detail.DetailViewModel
import com.example.memolog.feature.home.HomeFragment
import com.example.memolog.feature.home.HomeFragmentDirections
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

        binding.editText.requestFocus()

        // 키보드 보이기
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)

        binding.cancelBtn.setOnClickListener {
            it.findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun initViewModel() {
        viewModelFactory = ViewModelFactory(MemoRepository())
        searchViewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
    }
}