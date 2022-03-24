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
import com.example.memolog.currentDate
import com.example.memolog.databinding.FragmentAddBinding
import com.example.memolog.databinding.FragmentDetailBinding
import com.example.memolog.repository.MemoRepository
import com.example.memolog.repository.entity.Memo

class DetailFragment : Fragment(){

    private lateinit var binding: FragmentDetailBinding
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var detailModel: DetailModel
    private var isEditMode = MutableLiveData(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        initViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MemoDebug", "DetailFragment::onViewCreated-()")

        val args: DetailFragmentArgs by navArgs()
        val memoId = args.memoId

        detailModel.getOneMemo(memoId) { memo ->
            binding.updatedTime.text = memo.updatedTime
            binding.textTitle.text = memo.title
            binding.textContent.text = memo.content
        }

        isEditMode.observe(viewLifecycleOwner){ isEditMode ->
            if(isEditMode){
                binding.editBtn.visibility = View.VISIBLE
                binding.editTitle.visibility = View.VISIBLE
                binding.editContent.visibility = View.VISIBLE

                binding.textTitle.visibility = View.GONE
                binding.textContent.visibility = View.GONE
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

    }

    private fun initViewModel(){
        viewModelFactory = ViewModelFactory(MemoRepository())
        detailModel = ViewModelProvider(this, viewModelFactory).get(DetailModel::class.java)
    }

}