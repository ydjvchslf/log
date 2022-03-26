package com.example.memolog.feature.detail

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.Gravity.apply
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat.apply
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.example.memolog.R
import com.example.memolog.ViewModelFactory
import com.example.memolog.databinding.FragmentAddBinding
import com.example.memolog.databinding.FragmentDetailBinding
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo
import android.view.inputmethod.InputMethodManager

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.os.Build
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.example.memolog.getCurrentTime


class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var detailModel: DetailModel
    private var isEditMode = MutableLiveData(false)
    private var memoId = 0L
    private lateinit var backPressCallback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        initViewModel()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MemoDebug", "DetailFragment::onViewCreated-()")

        val args: DetailFragmentArgs by navArgs()
        memoId = args.memoId

        detailModel.getOneMemo(memoId) { memo ->
            binding.updatedTime.text = memo.updatedTime
            binding.textTitle.text = memo.title
            binding.textContent.text = memo.content
        }

        isEditMode.observe(viewLifecycleOwner) { isEditMode ->
            if (isEditMode) {
                binding.editBtn.visibility = View.VISIBLE
                binding.editTitle.visibility = View.VISIBLE
                binding.editContent.visibility = View.VISIBLE

                binding.textTitle.visibility = View.GONE
                binding.textContent.visibility = View.GONE
            } else {
                binding.editBtn.visibility = View.INVISIBLE
                binding.editTitle.visibility = View.INVISIBLE
                binding.editContent.visibility = View.INVISIBLE

                binding.textTitle.visibility = View.VISIBLE
                binding.textContent.visibility = View.VISIBLE

                // 수정 내용 -> textView 세팅
                binding.textTitle.text = binding.editTitle.text
                binding.textContent.text = binding.editContent.text

                // TODO:: 키보드 내리기 추가할 것
            }
        }

        binding.textTitle.setOnClickListener {
            isEditMode.value = true
            binding.editTitle.setText(binding.textTitle.text.toString())
            binding.editContent.setText(binding.textContent.text.toString())
        }

        binding.textContent.setOnClickListener {
            isEditMode.value = true
            binding.editTitle.setText(binding.textTitle.text.toString())
            binding.editContent.setText(binding.textContent.text.toString())
        }

        // edit 버튼 -> 메모 업데이트
        binding.editBtn.setOnClickListener {
            detailModel.getOneMemo(memoId) { current ->
                current.title = binding.editTitle.text.toString()
                current.content = binding.editContent.text.toString()
                val currentTime = getCurrentTime()
                current.updatedTime = currentTime

                detailModel.updateMemo(current)
                isEditMode.postValue(false)
            }
        }

        // back 버튼 -> 메모 업데이트
        binding.backBtn.setOnClickListener {
            //memoId
            reviseMemo()
        }
    }

    private fun initViewModel() {
        viewModelFactory = ViewModelFactory(MemoRepository())
        detailModel = ViewModelProvider(this, viewModelFactory).get(DetailModel::class.java)
    }

    // back key -> 메모 업데이트
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(findNavController().currentDestination?.id == R.id.detailFragment) {
                    Log.d("MemoDebug", "DetailFragment::handleOnBackPressed-()")
                    reviseMemo()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    override fun onDetach() {
        super.onDetach()
        backPressCallback.remove()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reviseMemo(){
        if (isEditMode.value == true) {
            detailModel.getOneMemo(memoId) { current ->
                current.title = binding.editTitle.text.toString()
                current.content = binding.editContent.text.toString()
                val currentTime = getCurrentTime()
                current.updatedTime = currentTime

                detailModel.updateMemo(current)
                isEditMode.postValue(false)
            }
        } else {
            detailModel.getOneMemo(memoId) { current ->
                current.title = binding.textTitle.text.toString()
                current.content = binding.textContent.text.toString()
                val currentTime = getCurrentTime()
                current.updatedTime = currentTime

                detailModel.updateMemo(current)
                isEditMode.postValue(false)
            }
        }
        this.findNavController().navigate(R.id.homeFragment)
    }
}