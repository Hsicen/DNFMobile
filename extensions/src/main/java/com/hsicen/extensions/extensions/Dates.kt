package com.hsicen.extensions.extensions

import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


const val FORMAT_TIMESTAMP = "MMM dd, yyyy HH:mm:ss a"
const val FORMAT_LONG = "yyyy-MM-dd HH:mm:ss"
const val FORMAT_LONG2 = "yyyy-MM-dd_HH-mm-ss-MMM"
const val FORMAT_yMdHm = "yyyy-MM-dd HH:mm"
const val FORMAT_yMdHm1 = "yyyy/MM/dd HH:mm"
const val FORMAT_SHORT = "yyyy-MM-dd"
const val FORMAT_SHORT2 = "yyyy/MM/dd"
const val FORMAT_SHORT3 = "yyyy.MM.dd"
const val FORMAT_SHORT4 = "yyyy-MM"
const val FORMAT_NUMBER = "yyyyMMdd"
const val FORMAT_NUMBER_MONTH = "yyyyMM"
const val FORMAT_CN_LONGS = "yyyy年MM月dd日 HH:mm"
const val FORMAT_CN_LONG = "yyyy年MM月dd日 HH:mm:ss"
const val FORMAT_CN_SHORT = "yyyy年MM月dd日"
const val FORMAT_CN_SHORT1 = "yyyy年M月d日"
const val FORMAT_TIME = "HH:mm"
const val FORMAT_TIME_NE = "HH:mm"
const val FORMAT_yyyyMM = "yyyy-MM"
const val FORMAT_hms = "HH:mm:ss"
const val FORMAT_CN_YM = "yyyy年MM月"
const val FORMAT_CN_YM2 = "yyyy年M月"
const val FORMAT_CN_YMD = "yyyy年MM月dd日"
const val FORMAT_CN_MD = "M月d日"
const val FORMAT_CN_MMDD = "MM月dd日"
const val FORMAT_CN_d_Hm = "dd日 HH:mm"
const val FORMAT_MdHm = "MM-dd HH:mm"
const val FORMAT_Md = "MM-dd"
const val FORMAT_Md2 = "MM/dd"
const val FORMAT_Md3 = "M.dd"
const val FORMAT_CN_M = "MM月"
const val FORMAT_d = "d"
const val FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val FORMAT_UTC1 = "yyyy-MM-dd'T'HH:mm:ss"
const val FORMAT_WEEK = "EEE"
const val FORMAT_YMD = "yyyy.MM.dd"
const val FORMAT_YM = "yyyy.MM"
const val FORMAT_Y = "yyyy"
const val FORMAT_H = "HH"
const val FORMAT_M = "mm"
const val FORMAT_YMDH = "yyyy.MM.dd HH:mm"
const val FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ"

fun Date.d2s(format: String): String? = formatter(format).format(this)

fun String.s2d(format: String): Date? =
    runCatching {
        formatter(format).parse(this)
    }.onFailure {
        Logger.e(it, "")
    }.getOrNull()

fun String.cnLong2d() = s2d(FORMAT_CN_LONG)
fun String.cnShort2d() = s2d(FORMAT_CN_SHORT)
fun String.long2d() = s2d(FORMAT_LONG)
fun String.middle2d() = s2d(FORMAT_yMdHm)
fun String.number2d() = s2d(FORMAT_NUMBER)
fun String.utc2d() = s2d(FORMAT_UTC)
fun String.utc2d1() = s2d(FORMAT_UTC1)
fun String.short2d() = s2d(FORMAT_SHORT)
fun String.short2d4() = s2d(FORMAT_SHORT4)
fun String.timestamp2d() = s2d(FORMAT_TIMESTAMP)
fun String.iso8601Tod() = s2d(FORMAT_ISO8601)

fun Date.d2cnLong() = d2s(FORMAT_CN_LONG)
fun Date.d2cnShort() = d2s(FORMAT_CN_SHORT)
fun Date.d2long() = d2s(FORMAT_LONG)
fun Date.middle2d() = d2s(FORMAT_yMdHm)
fun Date.d2middle() = d2s(FORMAT_yMdHm1)
fun Date.d2ymd() = d2s(FORMAT_YMD)
fun Date.d2number() = d2s(FORMAT_NUMBER)
fun Date.d2numberMonth() = d2s(FORMAT_NUMBER_MONTH)
fun Date.d2utc() = d2s(FORMAT_UTC)
fun Date.d2utc1() = d2s(FORMAT_UTC1)
fun Date.d2Iso8601() = d2s(FORMAT_ISO8601)
fun Date.d2short() = d2s(FORMAT_SHORT)
fun Date.d2short4() = d2s(FORMAT_SHORT4)
fun Date.d2year() = d2s(FORMAT_Y)
fun Date.d2timestamp() = d2s(FORMAT_TIMESTAMP)
fun Date.d2hs() = d2s(FORMAT_TIME)

fun Long.toDate() = Date(this)

/**
 * 根据 format 生成对应的 SimpleDateFormat
 */
private fun formatter(format: String) = SimpleDateFormat(format, Locale.getDefault())


fun Date.addDay(step: Int) = applyWrap { add(Calendar.DAY_OF_MONTH, step) }
fun Date.addMouth(step: Int) = applyWrap { add(Calendar.MONTH, step) }
fun Date.addYear(step: Int) = applyWrap { add(Calendar.YEAR, step) }
fun Date.addHour(step: Int) = applyWrap { add(Calendar.HOUR_OF_DAY, step) }

fun Date.isToday(): Boolean {
    val calendar = Calendar.getInstance().apply { time = this@isToday }
    val today = Calendar.getInstance()

    return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
}

/**
 * 将Date向上提到Calendar中处理完再降回Date
 */
private fun Date.applyWrap(action: Calendar.() -> Unit) = Calendar.getInstance()
    .apply {
        time = this@applyWrap
        action()
    }
    .time

/**
 * 计算时间相减（以天为单位）
 */
operator fun Date.minus(other: Date): Long {
    return TimeUnit.DAYS.convert(this.time - other.time, TimeUnit.MILLISECONDS)
}
