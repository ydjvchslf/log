package com.example.memolog.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.memolog.R
import com.example.memolog.databinding.MemoItemBinding
import com.example.memolog.feature.favorite.FavoriteFragmentDirections
import com.example.memolog.feature.home.HomeFragmentDirections
import com.example.memolog.feature.search.SearchFragmentDirections
import com.example.memolog.model.MemoModel
import com.example.memolog.repository.MemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    var memoList = ArrayList<MemoModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListData(data: ArrayList<MemoModel>) {
        this.memoList = data
        notifyDataSetChanged()
    }

    fun clear() {
        this.memoList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//       val inflater = LayoutInflater.from(parent.context).inflate(R.layout.memo_item, parent, false)
//        return MyViewHolder(inflater)
        val binding = MemoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return memoList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(memoList[position])
    }

    class MyViewHolder(val binding: MemoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val memoRepository = MemoRepository()

        fun bind(memo: MemoModel) {
            binding.title.text = memo.title

            if (memo.isFavorite) { // ????????? ??????
                binding.unlikeBtn.visibility = View.VISIBLE
                binding.likeBtn.visibility = View.INVISIBLE
            } else {
                binding.unlikeBtn.visibility = View.INVISIBLE
                binding.likeBtn.visibility = View.VISIBLE
            }

            if (memo.isLocked) binding.lockBtn.visibility = View.VISIBLE else binding.lockBtn.visibility = View.INVISIBLE

            // ???????????? ??????
            binding.title.setOnClickListener {
                if(memo.isLocked) { // ?????? ???????????? ??????
                    // ???????????? ?????????????????? ?????? ????????? ??????, true ??? ?????? ??????O, false ??????X
                    showCheckPwDialog { password ->
                        if (password.length == 4 && memo.password == password) {
                            when (it.findNavController().currentDestination?.id) {
                                R.id.homeFragment -> {
                                    it.findNavController()
                                        .navigate(HomeFragmentDirections.actionHomeToDetail(memo.id))
                                }
                                R.id.favoriteFragment -> {
                                    it.findNavController()
                                        .navigate(
                                            FavoriteFragmentDirections.actionFavoriteToDetail(memo.id))
                                }
                                R.id.searchFragment -> {
                                    it.findNavController()
                                        .navigate(
                                            SearchFragmentDirections.actionSearchToDetail(memo.id))
                                }
                            }
                        }else{
                            binding.inputPw.text = null
                            Toast.makeText(binding.root.context, "??????????????? ???????????? ????????????", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    when (it.findNavController().currentDestination?.id) {
                        R.id.homeFragment -> {
                            it.findNavController()
                                .navigate(HomeFragmentDirections.actionHomeToDetail(memo.id))
                        }
                        R.id.favoriteFragment -> {
                            it.findNavController()
                                .navigate(
                                    FavoriteFragmentDirections.actionFavoriteToDetail(memo.id))
                        }
                        R.id.searchFragment -> {
                            it.findNavController()
                                .navigate(
                                    SearchFragmentDirections.actionSearchToDetail(memo.id))
                        }
                    }
                }
            }

            // ????????? ??????
            binding.likeBtn.setOnClickListener {
                Log.d("MemoDebug", "????????? ??????")
                //Toast.makeText(binding.root.context, "id : ${memo.id}", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    memoRepository.setFavorite(memo.id, !memo.isFavorite)
                }
            }

            // ????????? ??????
            binding.unlikeBtn.setOnClickListener {
                Log.d("MemoDebug", "????????? ??????")
                //Toast.makeText(binding.root.context, "id : ${memo.id}", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    memoRepository.setFavorite(memo.id, !memo.isFavorite)
                }
            }
        }// fun bind()

        private fun showCheckPwDialog(password: (String) -> Unit){
            CoroutineScope(Dispatchers.IO).launch {

                Handler(Looper.getMainLooper()).post {
                    if (binding.inputPw.parent != null) {
                        (binding.inputPw.parent as ViewGroup).removeView(binding.inputPw)
                        binding.inputPw.visibility = View.VISIBLE
                        binding.inputPw.requestFocus()
                    }

                    val dialogBuilder = AlertDialog.Builder(binding.root.context)
                        .setMessage("??????????????? ??????????????????")
                        .setView(binding.inputPw)
                        .setPositiveButton("yes") { _, _ ->
                            val inputPw = binding.inputPw.text.toString()
                            Log.d("MemoDebug", "inputPw: $inputPw")
                            password.invoke(inputPw)

                        }
                        .setNegativeButton("cancel") { _, _ ->
                            binding.inputPw.text = null
                            return@setNegativeButton
                        }
                        .show()
                    val window = dialogBuilder.window
                    window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
                    window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }
            }
        }
    }
}