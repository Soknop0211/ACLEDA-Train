package com.example.acledaproject.ui.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.acledaproject.R
import com.example.acledaproject.model.ItemImageSliderModel

class ImageSliderAdapter(
    private val mContext: Context,
    private val theSlideItemsModelClassList: List<ItemImageSliderModel>,
    private val mListener: (ItemImageSliderModel) -> Unit
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val sliderLayout = inflater.inflate(R.layout.custom_image_slide_model, null)

        val mItem = theSlideItemsModelClassList[position]
        // Init View
        val mImageView = sliderLayout.findViewById<ImageView>(R.id.imageViewPager)
        val addressTv = sliderLayout.findViewById<TextView>(R.id.descriptionTxt)
        val mView = sliderLayout.findViewById<View>(R.id.mView)
        val mTitleLayout = sliderLayout.findViewById<LinearLayout>(R.id.mTitleLayout)

        // Init Data
        Glide.with(mContext)
            .load(mItem.logo)
            .into(mImageView)

        addressTv.text = mItem.description

        val layoutParams: ViewGroup.LayoutParams = mView.layoutParams

        // Set Bottom Layout
        widthHeightLayout(mTitleLayout) { _, height ->
            layoutParams.height = height

            mView.layoutParams = layoutParams
        }

        container.addView(sliderLayout)
        return sliderLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return theSlideItemsModelClassList.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }

    private fun widthHeightLayout(
        layout: View,
        onCallBackListener: (width: Int, height: Int) -> Unit
    ) {
        val vto = layout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                onCallBackListener.invoke(layout.measuredWidth, layout.measuredHeight)
            }
        })
    }
}