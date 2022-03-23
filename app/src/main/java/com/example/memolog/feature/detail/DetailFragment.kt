package com.example.memolog.feature.detail

import android.content.Context
import android.os.Bundle
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
    lateinit var detailModel: DetailModel

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
        //Toast.makeText(context, "id: $memoId", Toast.LENGTH_LONG).show()

        detailModel.getOneMemo(memoId) { memo ->
            binding.updatedTime.text = memo.updatedTime
            binding.title.text = memo.title
            binding.content.text = memo.content
        }

    }

    private fun initViewModel(){
        viewModelFactory = ViewModelFactory(MemoRepository())
        detailModel = ViewModelProvider(this, viewModelFactory).get(DetailModel::class.java)
    }

}