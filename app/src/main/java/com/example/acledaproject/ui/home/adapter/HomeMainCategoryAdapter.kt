package com.example.acledaproject.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.acledaproject.R
import com.example.acledaproject.databinding.HomeMainItemLayoutBinding
import com.example.acledaproject.model.HomeItemModel
import com.example.acledaproject.utils.Util.Companion.widthHeightLayout

class HomeMainCategoryAdapter(
    private val mList: ArrayList<HomeItemModel>,
    private val mListener : (HomeItemModel) -> Unit
) : RecyclerView.Adapter<HomeMainCategoryAdapter.ViewHolder>() {

    class ViewHolder(private val binding: HomeMainItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var index = 0

        fun onBinding(mItem: HomeItemModel, mPosition: Int, mListener : (HomeItemModel) -> Unit) {
            binding.name.text = mItem.name
            binding.icon.setImageResource(mItem.logo)

            index++

            val layoutParams: ViewGroup.LayoutParams = binding.rightLine.layoutParams
            widthHeightLayout(binding.mLayout) { _, height ->
                layoutParams.height = height
                binding.rightLine.layoutParams = layoutParams
            }

            binding.bottomLine.visibility = if (mPosition > 5) View.GONE else View.VISIBLE

            if (index % 3 == 0) {
                index = 0
                binding.rightLine.visibility = View.GONE
            } else {
                binding.rightLine.visibility = View.VISIBLE
            }

            binding.mLayout.setOnClickListener { view ->
                // Delay Click
                view.isEnabled = false
                view.postDelayed({ view.isEnabled = true }, 500)

                mListener.invoke(mItem)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            HomeMainItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBinding(mList[position], position, mListener)
    }

}