package com.example.acledaproject.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.acledaproject.R
import com.example.acledaproject.databinding.NotificationItemLayoutBinding

class NotificationAdapter(private val mList: ArrayList<Int>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(private val binding: NotificationItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(position: Int) {
            if (position % 3 == 0){
                binding.iconBank.setImageResource(R.drawable.ic_qrcode_home)
            } else {
                binding.iconBank.setImageResource(R.drawable.acleda_logo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }

}