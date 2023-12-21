package com.example.acledaproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.fluentvalidator.context.ValidationResult
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
import java.io.Serializable

class EmvViewModel : ViewModel() {

    private val _qrCodeSt = MutableLiveData<String>()
    val qrCodeSt: LiveData<String> = _qrCodeSt

    private val qrCode = "00020101021230190015john_smith@devb5204599953038405405500.05802KH5910John Smith6010Phnom Penh62640111Invoice#0690314Coffee Khlaing0727Cooooooooooooooooooounter 299170013161406568381963040F76";

    private val qrCode1 = "00020101021215311234567812345678ABCDEFGHIJKLMNO29460015john_smith@dev\n" +
            "b0111855122334550208Dev Bank52045999530384054031005802KH5910John\n" +
            "Smith6010PHNOM PENH62670106#123450211855122334550311Coffee\n" +
            "Shop0709Cashier_10810Buy coffee64290002km0108ចន\n" +
            "ស្មីន0207ភ្នំពញ991700131688023959268630442A9"

    private val qrCode2 = "00020101021229180014jonhsmith@nbcq52045999530384054031.05802KH5910Jonh Smith6010Phnom Penh6304C297"

    fun encodeGenerateQR() {
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
        merchantPresentMode.setMerchantCity("PHNOM PENH")
        merchantPresentMode.merchantInformationLanguage = merchantInformationLanguage

        merchantPresentMode.setMerchantName("ACCOUNT NOP")
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

        _qrCodeSt.value = merchantPresentMode.toString()

        //0002010102110204000426160004hoge0104abcd520441115303156540523.7255020156035005
        //70155802CN5914BEST TRANSPORT6007BEIJING610712345676295010512345020567890030509
        //8760405543210505abcde0605fghij0705klmno0805pqres0905tuvxy5010000110101i6428000
        //2ZH0102北京0204最佳运输0304abcd65020080320016A011223344998877070812345678630432B
        //3

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

    fun testSuccessDecode(encodeString : String) {
        val encoded = ("00020101021102160004hoge0104abcd520441115303156540523"
                + ".7255020256035005802CN5914BEST TRANSPORT6007BEIJING6107123456762800205"
                + "678900305098760505abcde0705klmno0805pqres0903tuv1004abcd50160004123401"
                + "04ijkl64280002ZH0102北京0204最佳运输0304abcd65020080320016A0112233449988"
                + "7707081234567863046325")

        // Check encode invalid
        val validationResult : ValidationResult = Crc16Validate.validate(encodeString)
        if (!validationResult.isValid) {
            return
        }

        val merchantPresentedMode = DecoderMpm.decode(
            encoded,
            MerchantPresentedMode::class.java
        )

        val countryCode = merchantPresentedMode.countryCode.value
        val merchantCategoryCode = merchantPresentedMode.merchantCategoryCode.value
        val merchantCity = merchantPresentedMode.merchantCity.value
        val merchantName = merchantPresentedMode.merchantName.value

        var transactionAmount: String? = null
        if (merchantPresentedMode.transactionAmount != null) {
            transactionAmount = (merchantPresentedMode.transactionAmount.value ?: 0.0).toString()
        }


        /*val payloadFormatIndicator = merchantPresentedMode.payloadFormatIndicator.value
        val pointOfInitiationMethod = merchantPresentedMode.pointOfInitiationMethod.value
        val postalCode = merchantPresentedMode.postalCode.value
        val tipOrConvenienceIndicator = merchantPresentedMode.tipOrConvenienceIndicator.value
        val transactionCurrency = merchantPresentedMode.transactionCurrency.value
        val valueOfConvenienceFeeFixed = merchantPresentedMode.valueOfConvenienceFeeFixed.value
        val merchantInformationLanguage = merchantPresentedMode.merchantInformationLanguage.value.merchantName.value
        */
        val decode = "DECODE"

        Log.d("logdebugdecode",
            "countryCode : $countryCode\n" +
                    "merchantName : $merchantName\n" +
                    "transactionAmount : ${transactionAmount ?: 0.0 }")

        /* val mDataDecode : MerchantPresentModel = DecoderMpm.decode(
             encodeString,
             MerchantPresentModel::class.java
         )

         Log.d("logdebugdecodemodel",
             "countryCode : ${mDataDecode.getCountryCode()}\n" +
                     "merchantName : ${mDataDecode.getMerchantName()}")*/
    }

    private val _listEncode = MutableLiveData<ArrayList<DataList>>()
    val listEncode: LiveData<ArrayList<DataList>> = _listEncode

    fun devideTag() {
        val encodeSts =
            "00020101021129360009khqr@aclb01090869710770206ACLEDA3920001185518499848010145204200053031165802KH5907SOK NOP6010PHNOM PENH6213020908697107763048E6D"

        val encodeSt =
            "00020101021129450016abaakhppxxx@abaa01090021849430208ABA Bank40390006abaP2P01121EF1D97EDE2702090021849435204000053038405802KH5907Nop SOK6010Phnom Penh6304CBB4"


        var j: Int

        val mList = ArrayList<DataList>()
        var mData = DataList()

        var i = 0
        while (i < encodeSt.length) {
            if (mData.num.isEmpty()){
                mData.num = encodeSt.substring(i, i + 2)
            } else if(mData.length.isEmpty()) {
                mData.length = encodeSt.substring(i, i + 2)
            } else {
                if (mData.num == "29" || mData.num == "39" || mData.num == "40") {
                    val mSubItemList = ArrayList<DataList>()
                    var mSubData = DataList()
                    val number = mData.length.trimStart('0').toInt()

                    val mItem = encodeSt.substring(i, i + number)
                    j = 0
                    while (j < mItem.length) {
                        if (mSubData.num.isEmpty()){
                            mSubData.num = mItem.substring(j, j + 2)
                        } else if(mSubData.length.isEmpty()) {
                            mSubData.length = mItem.substring(j, j + 2)
                        } else {
                            try {
                                val subNumber = mSubData.length.trimStart('0').toInt()
                                mSubData.valueItem = mItem.substring(j, j + subNumber)
                                mSubItemList.add(mSubData)
                                mSubData = DataList()
                                j += subNumber
                                continue
                            } catch (e: NumberFormatException) {
                                println("Invalid number format") // Output: Invalid number format
                            }
                        }
                        j += 2
                    }


                    i += number
                    mData.mList = mSubItemList
                    mList.add(mData)
                    mData = DataList()
                    continue
                } else {
                    try {
                        val number = mData.length.trimStart('0').toInt()
                        mData.valueItem = encodeSt.substring(i, i + number)
                        mList.add(mData)
                        mData = DataList()
                        i += number
                        continue
                    } catch (e: NumberFormatException) {
                        println("Invalid number format") // Output: Invalid number format
                    }
                }
            }

            i += 2

        }

        mList.forEach {
            Log.d("logdebugdecodemodel", it.num + " : " + it.length + "=>" + it.valueItem)
        }

        _listEncode.value = mList
    }


}

data class DataList(
    var num: String = "",
    var length: String = "",
    var valueItem: String = "",
    var mList : ArrayList<DataList> = ArrayList()
    ) : Serializable
