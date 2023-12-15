package com.example.acledaproject.ui.home

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseFragment
import com.example.acledaproject.databinding.FragmentHomeBinding
import com.example.acledaproject.model.HomeExtraModel
import com.example.acledaproject.model.HomeItemModel
import com.example.acledaproject.model.ItemImageSliderModel
import com.example.acledaproject.ui.CaptureScanActivity
import com.example.acledaproject.ui.adapter.HomeExtraCategoryAdapter
import com.example.acledaproject.ui.adapter.HomeMainCategoryAdapter
import com.example.acledaproject.ui.adapter.ImageSliderAdapter
import com.example.acledaproject.utils.urlResource
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.util.Timer
import java.util.TimerTask


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val layoutResource: Int get() = R.layout.fragment_home
    private var mListSlide : List<ItemImageSliderModel> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Main Category
        mBinding.itemListTop.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = HomeMainCategoryAdapter(getListHomeMenu()) {
                if (it.id == "scan_qr") {
                    alertOption(mActivity)
                }
            }
        }

        // Main After Category
        mBinding.itemListCenter.apply {
            layoutManager = LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false)
            adapter = HomeExtraCategoryAdapter(requireActivity(), getListExtraHomeMenu(requireContext()))
        }

        //Slide
        initHappeningSlide(requireContext())
    }

    private fun initHappeningSlide(mContext : Context) {
        val happeningNowList = listOf(
            ItemImageSliderModel(
                "loan",
                "Loan",
                "Get your loan for business expansion or other user quickly",
                R.drawable.img_loan
            ),
            ItemImageSliderModel(
                "qr_code",
                "QR Code",
                "Offer QR Code or POS for your business",
                R.drawable.img_scan
            ),
            ItemImageSliderModel(
                "interest",
                "Interest",
                "Get high interest for term deposits",
                R.drawable.img_interest
            ),
        )

        mListSlide = happeningNowList

        val mImageSliderAdapter = ImageSliderAdapter(mContext, happeningNowList) {

        }
        mBinding.viewPagerImageSlider.adapter = mImageSliderAdapter

        val timer = Timer()
        timer.scheduleAtFixedRate(TheSlideTimer(), 2000, 3000)
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPagerImageSlider, true)
    }

    inner class TheSlideTimer : TimerTask() {
        override fun run() {
            mActivity?.runOnUiThread {
                if (mBinding.viewPagerImageSlider.currentItem < mListSlide.size - 1) {
                    mBinding.viewPagerImageSlider.currentItem = mBinding.viewPagerImageSlider.currentItem + 1
                } else mBinding.viewPagerImageSlider.currentItem = 0
            }
        }
    }

    private fun getListHomeMenu(): ArrayList<HomeItemModel> {
        return arrayListOf(
            HomeItemModel(
                "payment",
                "Payments",
                R.drawable.ic_payment,
            ),
            HomeItemModel(
                "top_up",
                "Mobile Top-up",
                R.drawable.ic_mobile_topup,
            ),
            HomeItemModel(
                "transfers",
                "Transfers",
                R.drawable.ic_transfer,
            ),
            HomeItemModel(
                "pay_me",
                "Pay-Me",
                R.drawable.ic_payme,
            ),
            HomeItemModel(
                "scan_qr",
                "Scan QR",
                R.drawable.ic_scan_qr,
            ),
            HomeItemModel(
                "account",
                "Accounts",
                R.drawable.ic_account_payment,
            ),
            HomeItemModel(
                "deposits",
                "Deposits",
                R.drawable.baseline_auto_graph_24,
            ),
            HomeItemModel(
                "loan",
                "Loans",
                R.drawable.ic_loan,
            ),
            HomeItemModel(
                "quick_cash",
                "Quick Cash",
                R.drawable.ic_quick_cash,
            )
        )
    }

    private fun getListExtraHomeMenu(context: Context): ArrayList<HomeExtraModel> {
        return arrayListOf(
            HomeExtraModel(
                "favorites",
                "Favorites",
                "Save recipient information for quick transaction.",
                false,
                context.packageName.urlResource(R.drawable.ic_favorite),
            ),

            HomeExtraModel(
                "exchange_rate",
                "Exchange Rate",
                "",
                true,
                context.packageName.urlResource(R.drawable.ic_currency_exchange),
            ),

            HomeExtraModel(
                "other_service",
                "Other Service\nWith Partners",
                "",
                false,
                context.packageName.urlResource(R.drawable.ic_dashboard),
            ),

            HomeExtraModel(
                "promotion",
                "Promotions",
                "More discounts and\nspecial offer from\nACLEDA's Partners.",
                false,
                context.packageName.urlResource(R.drawable.ic_promotion),
            ),
        )
    }

    private fun alertOption(mContext: Context?) {
        if (mContext == null) return

        val options = arrayOf("Zxing Lib", "MLKit Lib")

        val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
        builder.setTitle("Scan Option :")
        builder.setItems(options) { dialog, position ->
            if (position == 0) {
                gotoScan()
            } else if (position == 1) {

            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun gotoScan() {
        val options = ScanOptions()
        options.setPrompt("Scan QR Code")
        options.setCameraId(0) // Use a specific camera of the device

        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = CaptureScanActivity::class.java
        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            Toast.makeText(mActivity, result.contents, Toast.LENGTH_LONG).show()
        }
    }
}