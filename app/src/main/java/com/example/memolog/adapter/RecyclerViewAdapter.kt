package com.example.memolog.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.memolog.databinding.MemoItemBinding
import com.example.memolog.feature.home.HomeFragmentDirections
import com.example.memolog.model.MemoModel
import com.example.memolog.repository.MemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.logging.Handler

class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    var memoList = ArrayList<MemoModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListData(data: ArrayList<MemoModel>){
        this.memoList = data
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

    class MyViewHolder(val binding: MemoItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(memo: MemoModel){
            val memoRepository = MemoRepository()
            binding.title.text = memo.title

            if(memo.isFavorite){ // 좋아요 상태
                binding.unlikeBtn.visibility = View.VISIBLE
                binding.likeBtn.visibility = View.INVISIBLE
            }else{
                binding.unlikeBtn.visibility = View.INVISIBLE
                binding.likeBtn.visibility = View.VISIBLE
            }

            // 상세화면 이동
            binding.title.setOnClickListener {
                Toast.makeText(binding.root.context, "id : ${memo.id}", Toast.LENGTH_SHORT).show()
                //it.findNavController().navigate(R.id.action_home_to_detail)
                it.findNavController().navigate(HomeFragmentDirections.actionHomeToDetail(memo.id))
            }

            // 좋아요 버튼
            binding.likeBtn.setOnClickListener{
                Toast.makeText(binding.root.context, "id : ${memo.id}", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        memoRepository.setFavorite(memo.id, !memo.isFavorite)
                    }
            }

            // 좋아요 해제
            binding.unlikeBtn.setOnClickListener{
                Toast.makeText(binding.root.context, "id : ${memo.id}", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        memoRepository.setFavorite(memo.id, !memo.isFavorite)
                    }
            }
        }
    }
}