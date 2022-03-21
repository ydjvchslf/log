package com.example.memolog.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memolog.databinding.MemoItemBinding
import com.example.memolog.model.MemoModel

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

    class MyViewHolder(val binding: MemoItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(memo: MemoModel){
            binding.title.text = memo.title

            binding.title.setOnClickListener {
                Toast.makeText(binding.root.context, "id : ${memo.id}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}