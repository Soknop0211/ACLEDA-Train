package com.example.acledaproject.ui.qrcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import br.com.fluentvalidator.context.ValidationResult
import br.com.fluentvalidator.predicate.ComparablePredicate.equalTo
import com.bumptech.glide.Glide
import com.emv.qrcode.core.model.mpm.TagLengthString
import com.emv.qrcode.decoder.mpm.DecoderMpm
import com.emv.qrcode.model.mpm.AdditionalDataField
import com.emv.qrcode.model.mpm.AdditionalDataFieldTemplate
import com.emv.qrcode.model.mpm.MerchantAccountInformationReserved
import com.emv.qrcode.model.mpm.MerchantAccountInformationReservedAdditional
import com.emv.qrcode.model.mpm.MerchantAccountInformationTemplate
import com.emv.qrcode.model.mpm.MerchantInformationLanguage
import com.emv.qrcode.model.mpm.MerchantInformationLanguageTemplate
import com.emv.qrcode.model.mpm.MerchantPresentedMode
import com.emv.qrcode.model.mpm.PaymentSystemSpecific
import com.emv.qrcode.model.mpm.PaymentSystemSpecificTemplate
import com.emv.qrcode.model.mpm.Unreserved
import com.emv.qrcode.model.mpm.UnreservedTemplate
import com.emv.qrcode.validators.Crc16Validate
import com.example.acledaproject.R
import com.example.acledaproject.base.BaseBindActivity
import com.example.acledaproject.databinding.ActivityEmvQrCodeBinding
import com.example.acledaproject.utils.Util


class EmvQrCodeActivity : BaseBindActivity<ActivityEmvQrCodeBinding>() {

    override val layoutId = R.layout.activity_emv_qr_code

    companion object {
        fun start(mContext : Context) {
            mContext.startActivity(Intent(mContext, EmvQrCodeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val additionalDataField = addtionalDataField

        val merchanAccountInformationReservedAdditional = merchanAccountInformationReservedAdditional
        val merchanAccountInformationReserved = merchanAccountInformationReserved
        val merchantInformationLanguage = merchantInformationLanguage
        val unreserved = unreserved

        val rFUforEMVCo = TagLengthString("65", "00")

        val merchantPresentMode = MerchantPresentedMode()
        merchantPresentMode.additionalDataField = additionalDataField

        merchantPresentMode.setCountryCode("KH")
        merchantPresentMode.setMerchantCategoryCode("4111")
        merchantPresentMode.setMerchantCity("BEIJING")
        merchantPresentMode.merchantInformationLanguage = merchantInformationLanguage

        merchantPresentMode.setMerchantName("BEST TRANSPORT -> NOP")
        merchantPresentMode.setPayloadFormatIndicator("01")
        merchantPresentMode.setPointOfInitiationMethod("11")
        merchantPresentMode.setPostalCode("1234567")
        merchantPresentMode.setTipOrConvenienceIndicator("01")
        merchantPresentMode.setTransactionAmount("23.72")
        merchantPresentMode.setTransactionCurrency("156")
        merchantPresentMode.setValueOfConvenienceFeeFixed("500")
        merchantPresentMode.setValueOfConvenienceFeePercentage("5")
        merchantPresentMode.addMerchantAccountInformation(merchanAccountInformationReserved)

        merchantPresentMode.addMerchantAccountInformation(
            merchanAccountInformationReservedAdditional
        )
        merchantPresentMode.addRFUforEMVCo(rFUforEMVCo)
        merchantPresentMode.addUnreserved(unreserved)

        println(merchantPresentMode.toString())
        //0002010102110204000426160004hoge0104abcd520441115303156540523.7255020156035005
        //70155802CN5914BEST TRANSPORT6007BEIJING610712345676295010512345020567890030509
        //8760405543210505abcde0605fghij0705klmno0805pqres0905tuvxy5010000110101i6428000
        //2ZH0102北京0204最佳运输0304abcd65020080320016A011223344998877070812345678630432B
        //3


        //**** Init Qr ****
        mBinding.iconBack.setOnClickListener {
            finish()
        }

        // Init Generate Qr
        val qrCodeImageBitmap: Bitmap? = Util.getQRCodeImage512(512, merchantPresentMode.toString())
        if (qrCodeImageBitmap != null) {
            mBinding.imageQr.setImageBitmap(qrCodeImageBitmap)
        }

        // Decode
        testSuccessDecode(merchantPresentMode.toString())
    }

    private val merchanAccountInformationReserved: MerchantAccountInformationTemplate
        // Primitive Payment System Merchant Account Information (IDs "02" to "25")
        get() {
            val merchantAccountInformationValue = MerchantAccountInformationReserved("0004")
            return MerchantAccountInformationTemplate("02", merchantAccountInformationValue)
        }

    private val merchanAccountInformationReservedAdditional: MerchantAccountInformationTemplate
        // Merchant Account Information Template (IDs "26" to "51")
        get() {
            val paymentNetworkSpecific = TagLengthString()
            paymentNetworkSpecific.tag = "01"
            paymentNetworkSpecific.value = "abcd"
            val merchantAccountInformationValue = MerchantAccountInformationReservedAdditional()
            merchantAccountInformationValue.setGloballyUniqueIdentifier("hoge")
            merchantAccountInformationValue.addPaymentNetworkSpecific(paymentNetworkSpecific)
            return MerchantAccountInformationTemplate("26", merchantAccountInformationValue)
        }

    private val unreserved: UnreservedTemplate get() {
            val contextSpecificData = TagLengthString()
            contextSpecificData.tag = "07"
            contextSpecificData.value = "12345678"
            val value = Unreserved()
            value.setGloballyUniqueIdentifier("A011223344998877")
            value.addContextSpecificData(contextSpecificData)
            val unreserved = UnreservedTemplate()
            unreserved.value = value
            unreserved.tag = "80"
            return unreserved
    }

    private val merchantInformationLanguage: MerchantInformationLanguageTemplate get() {
            val rFUforEMVCo = TagLengthString()
            rFUforEMVCo.tag = "03"
            rFUforEMVCo.value = "abcd"
            val merchantInformationLanguageValue = MerchantInformationLanguage()
            merchantInformationLanguageValue.setLanguagePreference("ZH")
            merchantInformationLanguageValue.setMerchantName("北京")
            merchantInformationLanguageValue.setMerchantCity("最佳运输")
            merchantInformationLanguageValue.addRFUforEMVCo(rFUforEMVCo)
            val merchantInformationLanguage = MerchantInformationLanguageTemplate()
            merchantInformationLanguage.value = merchantInformationLanguageValue
            return merchantInformationLanguage
    }

    private val addtionalDataField: AdditionalDataFieldTemplate get() {
            val paymentSystemSpecific = PaymentSystemSpecific()
            paymentSystemSpecific.setGloballyUniqueIdentifier("1")
            paymentSystemSpecific.addPaymentSystemSpecific(TagLengthString("01", "i"))
            val paymentSystemSpecificTemplate = PaymentSystemSpecificTemplate()
            paymentSystemSpecificTemplate.tag = "50"
            paymentSystemSpecificTemplate.value = paymentSystemSpecific
            val additionalDataFieldValue = AdditionalDataField()
            additionalDataFieldValue.setAdditionalConsumerDataRequest("tuvxy")
            additionalDataFieldValue.setBillNumber("12345")
            additionalDataFieldValue.setCustomerLabel("fghij")
            additionalDataFieldValue.setLoyaltyNumber("54321")
            additionalDataFieldValue.setMobileNumber("67890")
            additionalDataFieldValue.setPurposeTransaction("pqres")
            additionalDataFieldValue.setReferenceLabel("abcde")
            additionalDataFieldValue.setStoreLabel("09876")
            additionalDataFieldValue.setTerminalLabel("klmno")
            additionalDataFieldValue.addPaymentSystemSpecific(paymentSystemSpecificTemplate)
            val additionalDataField = AdditionalDataFieldTemplate()
            additionalDataField.value = additionalDataFieldValue
            return additionalDataField
    }


    private fun testSuccessDecode(encodeString : String) {
        val encoded = ("00020101021102160004hoge0104abcd520441115303156540523"
                + ".7255020256035005802CN5914BEST TRANSPORT6007BEIJING6107123456762800205"
                + "678900305098760505abcde0705klmno0805pqres0903tuv1004abcd50160004123401"
                + "04ijkl64280002ZH0102北京0204最佳运输0304abcd65020080320016A0112233449988"
                + "7707081234567863046325")

        // Check encode invalid
        val validationResult : ValidationResult = Crc16Validate.validate(encoded);
        if (!validationResult.isValid) {
            return
        }

        val merchantPresentedMode = DecoderMpm.decode(
            encodeString,
            MerchantPresentedMode::class.java
        )
        val countryCode = merchantPresentedMode.countryCode.value
        val merchantCategoryCode = merchantPresentedMode.merchantCategoryCode.value
        val merchantCity = merchantPresentedMode.merchantCity.value
        val merchantName = merchantPresentedMode.merchantName.value
        val payloadFormatIndicator = merchantPresentedMode.payloadFormatIndicator.value
        val pointOfInitiationMethod = merchantPresentedMode.pointOfInitiationMethod.value
        val postalCode = merchantPresentedMode.postalCode.value
        val tipOrConvenienceIndicator = merchantPresentedMode.tipOrConvenienceIndicator.value
        val transactionAmount = merchantPresentedMode.transactionAmount.value
        val transactionCurrency = merchantPresentedMode.transactionCurrency.value
        val valueOfConvenienceFeeFixed = merchantPresentedMode.valueOfConvenienceFeeFixed.value
        val decode = "DECODE"

        Log.d("logdebugdecode",
            "countryCode : $countryCode\n" +
                " merchantName : $merchantName")
    }

}