package com.example.acledaproject.ui.qrcode.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.acledaproject.databinding.DecodeEmvItemAdapterBinding
import com.example.acledaproject.viewmodel.DataList

class CodeDecodeEmvAdapter(
    private val mContext : Context,
    private val mList :ArrayList<DataList>):
    RecyclerView.Adapter<CodeDecodeEmvAdapter.ViewHolder>() {

    class ViewHolder(private val binding: DecodeEmvItemAdapterBinding): RecyclerView.ViewHolder(binding.root){

        fun onBinding(mContext: Context, mItem : DataList) {
            binding.txt.text = String.format("- %s : %s", mItem.num, mItem.length)
            binding.txtVal.text = mItem.valueItem

            // Main After Category
            binding.itemListCenter.apply {
                layoutManager = LinearLayoutManager(mContext)
                adapter = CodeDecodeEmvAdapter(mContext, mItem.mList)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DecodeEmvItemAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBinding(mContext, mList[position])
    }

}