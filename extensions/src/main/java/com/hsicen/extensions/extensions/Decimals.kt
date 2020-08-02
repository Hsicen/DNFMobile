package com.hsicen.extensions.extensions

import java.math.BigDecimal

/**
 * 作者：hsicen  2020/8/2 15:08
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：基本数据处理
 */

/**
 *  格式化数据,保留小数位
 *  @param digitNum 保留的位数, 默认保留2位，方便统一修改.
 *  @param formatStyle 默认四舍五入, ios的四舍五入是银行家算法
 */
fun Double?.digitFormat(digitNum: Int = 2, formatStyle: Int = BigDecimal.ROUND_HALF_UP): Double? {
    if (this == null) return null
    // 注意，double转bigDecimal只能用valueOf.
    return BigDecimal.valueOf(this).setScale(digitNum, formatStyle).toDouble()
}

fun Float?.digitFormat(digitNum: Int = 2, formatStyle: Int = BigDecimal.ROUND_HALF_UP): Float? {
    if (this == null) return null
    return BigDecimal("$this").setScale(digitNum, formatStyle).toFloat()
}

/**
 *  本项目中最高精度就处理到Double.
 *  安全除法,避免分母为0出现异常, 分母类型太多，所以都转String
 *  @param denominator 分母
 */
fun Double?.safeDiv(denominatorStr: String?): Double? {
    val denominator = denominatorStr?.toDoubleOrNull() ?: return null
    return if (denominator == 0.0) {
        null
    } else {
        this?.div(denominator)
    }
}

fun Float?.safeDiv(denominator: Double?): Double? {
    return if (denominator == null || denominator == 0.0) {
        null
    } else {
        this?.div(denominator)
    }
}

fun Float?.safeDiv(denominator: Float?): Float? {
    return if (denominator == null || denominator == 0f) {
        null
    } else {
        this?.div(denominator)
    }
}

fun Float?.safeDiv(denominator: Int?): Float? {
    return if (denominator == null || denominator == 0) {
        null
    } else {
        this?.div(denominator)
    }
}