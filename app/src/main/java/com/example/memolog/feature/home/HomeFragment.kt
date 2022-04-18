package com.example.memolog.feature.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memolog.R
import com.example.memolog.ViewModelFactory
import com.example.memolog.adapter.RecyclerViewAdapter
import com.example.memolog.databinding.FragmentHomeBinding
import com.example.memolog.model.MemoModel
import com.example.memolog.repository.MemoRepository

class HomeFragment : Fragment(){

    private lateinit var binding: FragmentHomeBinding
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var homeViewModel: HomeViewModel
    private lateinit var backPressCallback: OnBackPressedCallback

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(findNavController().currentDestination?.id == R.id.homeFragment) {
                    Log.d("MemoDebug", "HomeFragment::handleOnBackPressed-()")
                    AlertDialog.Builder(context)
                        .setMessage("do you want to exit?")
                        .setPositiveButton("yes") { _, _ ->
                            activity?.finish()
                        }
                        .setNegativeButton("no") { _, _ ->
                            // do nothing
                        }
                        .show()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun onDetach() {
        super.onDetach()
        backPressCallback.remove()
    }
}