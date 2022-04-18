package com.example.memolog.feature.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memolog.R
import com.example.memolog.ViewModelFactory
import com.example.memolog.adapter.RecyclerViewAdapter
import com.example.memolog.databinding.FragmentSearchBinding
import com.example.memolog.model.MemoModel
import com.example.memolog.repository.MemoRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

class SearchFragment: Fragment(){
    private lateinit var binding: FragmentSearchBinding
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var searchViewModel: SearchViewModel
    private val memoAdapter = RecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        initViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // editText 포커싱, 검색어 rxJava
        binding.editText.apply {
            requestFocus()
            addTextChangedListener(textWatcher)
        }

        // 키보드 보이기
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)

        // 리사이클러뷰
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = memoAdapter
            setHasFixedSize(true)
        }

        binding.cancelBtn.setOnClickListener {
            it.findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun initViewModel() {
        viewModelFactory = ViewModelFactory(MemoRepository())
        searchViewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
    }

    val textChange: PublishSubject<String> = PublishSubject.create()

    private val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            Log.d("MemoDebug", "SearchFragment::afterTextChanged-()")
            textChange.onNext(s.toString())

            textChange
                .debounce(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { keyword ->
                    //binding.textView.text = it
                    if(keyword.isEmpty()){
                        return@doOnNext
                    }
                    searchViewModel.memoList.observe(viewLifecycleOwner){ memoList ->
                        Log.d("MemoDebug", "memoList.size : ${memoList?.size}")

                        memoAdapter.clear()

                        var searchList = arrayListOf<MemoModel>()

                        memoList?.forEach { memo ->
                            val result = memo.content.contains(keyword)
                            Log.d("MemoDebug", "memo.id : ${memo.id}")
                            Log.d("MemoDebug", "검색어 내용에 포함? : $result")
                            if(result){
                                val memoModel = MemoModel.fromEntity(memo)
                                searchList.add(memoModel)
                                memoAdapter.setListData(searchList)
                            }
                        }
                    }

                }
                .subscribe()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    }

}