package com.example.acledaproject.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.acledaproject.R
import com.example.acledaproject.databinding.HomeExtraCategoryLayoutBinding
import com.example.acledaproject.model.HomeExtraModel

class HomeExtraCategoryAdapter(
    private val mActivity : Activity,
    private val mList :ArrayList<HomeExtraModel>):
    RecyclerView.Adapter<HomeExtraCategoryAdapter.ViewHolder>() {

    class ViewHolder(private val mActivity : Activity, private val binding: HomeExtraCategoryLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun onBinding(mItem : HomeExtraModel) {
            binding.nameTv.text = mItem.name
            binding.descriptionTv.text = mItem.description

            Glide.with(binding.icon.context).load(mItem.logo)
                .placeholder(R.drawable.baseline_home_24)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.icon)

            binding.descriptionTv.visibility = if (mItem.isExchangeRate) View.INVISIBLE else View.VISIBLE
            binding.exchangeRateLayout.visibility = if (mItem.isExchangeRate) View.VISIBLE else View.INVISIBLE

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomeExtraCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(mActivity, binding)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBinding(mList[position])
    }

}