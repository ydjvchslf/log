package com.example.memolog.feature.add

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Gravity.apply
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat.apply
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.memolog.R
import com.example.memolog.ViewModelFactory
import com.example.memolog.databinding.FragmentAddBinding
import com.example.memolog.getCurrentTime
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo

class AddFragment : Fragment(){

    private lateinit var binding: FragmentAddBinding
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var addViewModel: AddViewModel
    lateinit var toastLayout : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)

        // custom toast layout
        toastLayout = layoutInflater.inflate(R.layout.toast_layout, container, false)

        initViewModel()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addBtn.setOnClickListener {
            Log.d("MemoDebug", "AddFragment:: addBtn 클릭!")

            val title = binding.title.text.toString()
            val content = binding.content.text.toString()

            if(title.isEmpty() || content.isEmpty()){
                showToast()
                return@setOnClickListener
            }

            val memo = Memo(
                id = 0,
                title = binding.title.text.toString(),
                content = binding.content.text.toString(),
                isFavorite = false,
                isLocked = false,
                password = null,
                isBookmark= false,
                createdTime = getCurrentTime(),
                updatedTime = getCurrentTime()
            )
            addViewModel.insertMemo(memo)

            //초기화
            binding.title.text = null
            binding.content.text = null

            it.findNavController().navigate(R.id.action_add_to_home)
        }

        binding.loadBtn.setOnClickListener {
            Log.d("MemoDebug", "AddFragment:: loadBtn 클릭!")
            //addViewModel.getAllMemo()
            addViewModel.memoList.observe(viewLifecycleOwner){ memoList ->
                val listSize = memoList.size
                Log.d("MemoDebug", "AddFragment:: size : $listSize \n")
                Log.d("MemoDebug", "AddFragment:: memoList : $memoList")
            }
        }
    }

    private fun initViewModel(){
        viewModelFactory = ViewModelFactory(MemoRepository())
        addViewModel = ViewModelProvider(this, viewModelFactory).get(AddViewModel::class.java)
    }

    private fun showToast(){
        Toast(activity).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.CENTER, 0,0)
            view = toastLayout // TODO :: View 접근해서 text 바꿀 수 있나? 알아보기
        }.show()
    }
}