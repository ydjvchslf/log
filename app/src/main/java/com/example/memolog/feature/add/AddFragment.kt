package com.example.memolog.feature.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.memolog.R
import com.example.memolog.ViewModelFactory
import com.example.memolog.databinding.FragmentAddBinding
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo

class AddFragment : Fragment(){
    private lateinit var binding: FragmentAddBinding
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var addViewModel: AddViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)

        initViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addBtn.setOnClickListener {
            //it.findNavController().navigate(R.id.action_add_to_home)
            Log.d("MemoDebug", "AddFragment:: addBtn 클릭!")
            val memo = Memo(0, binding.title.text.toString(), binding.content.text.toString())
            addViewModel.insertMemo(memo)

            //초기화
            binding.title.text = null
            binding.content.text = null
        }

        binding.loadBtn.setOnClickListener {
            Log.d("MemoDebug", "AddFragment:: loadBtn 클릭!")
            //addViewModel.getAllMemo()
            addViewModel.memoList.observe(viewLifecycleOwner){ memoList ->
                val listSize = memoList.size
                binding.textView.text = listSize.toString()
                Log.d("MemoDebug", "AddFragment:: size : $listSize \n")
                Log.d("MemoDebug", "AddFragment:: memoList : $memoList")
            }
        }

    }

    private fun initViewModel(){
        viewModelFactory = ViewModelFactory(MemoRepository())
        addViewModel = ViewModelProvider(this, viewModelFactory).get(AddViewModel::class.java)
    }
}