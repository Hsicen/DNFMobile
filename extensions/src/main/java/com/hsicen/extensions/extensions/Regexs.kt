@file:Suppress("NOTHING_TO_INLINE", "WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET_ON_TYPE")

package com.hsicen.extensions.extensions

import android.annotation.SuppressLint
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.*

/**
 * 作者：hsicen  2020/8/2 15:21
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：常用正则验证
 */


private val WEIGHT = intArrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
private val VALID = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')

/** 中文. */
val REGEX_CHINESE = "^[\u4E00-\u9FA5]*$".toRegex()

/** 包含数字和字母. */
val REGEX_NUMBER_AND_LETTER = "^(?=.*[0-9])(?=.*[a-zA-Z])(.{8,20})\$".toRegex()


val DRIVER_LICENSE = ("/^(\\d{15}\$|^\\d{18}\$|^\\d{17}(\\d|X|x))\$/").toRegex()


val VEHICLE_PLATFORM =
    "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{5}[A-HJ-NP-Z0-9领使警学挂港澳试超]$".toRegex()

val VEHICLE_PLATFORM1 = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{5}".toRegex()


/**
 * 验证必须包含数字和字母.
 * @receiver String
 * @return Boolean
 */
inline fun String.verifyNumberAndLetter(): Boolean = REGEX_NUMBER_AND_LETTER.containsMatchIn(this)


/**
 * 验证中文.
 * @receiver String
 * @return Boolean
 */
inline fun String.verifyChinese(): Boolean = REGEX_CHINESE.containsMatchIn(this)

/**
 * 验证车牌号.
 * @receiver String
 * @return Boolean
 */
inline fun String.verifyVehiclePlatform(): Boolean =
    (VEHICLE_PLATFORM.containsMatchIn(this) || VEHICLE_PLATFORM1.containsMatchIn(this)) && (length == 7 || length == 8)

/**
 * 校验驾驶证信息
 */
fun String.isDriverLicense(): Boolean {
    if (trim().isEmpty()) {
        return false
    }
    if (trim().length != 15 && trim().length != 18) {
        return false
    }
    return true
}


/**
 * 验证身份证号码
 * 居民身份证号码15位或18位，最后一位可能是数字或字母
 * @return 验证成功返回true，验证失败返回false
 */
fun String.checkIdCard(): Boolean {
    if (isEmpty()) {
        return false
    }
    return when (length) {
        15 -> try {
            isOldCNIDCard(this)
        } catch (e: NumberFormatException) {
            Logger.e(e.message!!)
            false
        }
        18 -> isNewCNIDCard(this)
        else -> false
    }
}


private fun isNewCNIDCard(numbers: String): Boolean {
    var number = numbers
    number = number.toUpperCase(Locale.getDefault())
    var sum = 0
    for (i in WEIGHT.indices) {
        val cell = Character.getNumericValue(number[i])
        sum += WEIGHT[i] * cell
    }
    val index = sum % 11
    return VALID[index] == number[17]
}

@SuppressLint("SimpleDateFormat")
private fun isOldCNIDCard(numbers: String): Boolean {
    val yymmdd = numbers.substring(6, 11)
    val aPass = numbers == java.lang.Long.parseLong(numbers).toString()
    var yPass = true
    try {
        SimpleDateFormat("yyMMdd").parse(yymmdd)
    } catch (e: Exception) {
        Logger.e(e.message!!)
        yPass = false
    }
    return aPass && yPass
}



