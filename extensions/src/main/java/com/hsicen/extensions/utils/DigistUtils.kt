package com.hsicen.extensions.utils

import android.text.InputType
import android.text.method.NumberKeyListener

/**
 * 作者：hsicen  2020/8/2 11:16
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：指定键盘输入的字符串
 */
object DigitUtils {

    fun digitsKeyListener(
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        digit: String = "0123456789zxcvbnmasdfghjklqwertyuiopZXCVBNMASDFGHJKLQWERTYUIOP"
    ): NumberKeyListener {
        return object : NumberKeyListener() {

            /*** 指定键盘类型*/
            override fun getInputType(): Int {
                return inputType
            }

            /*** 指定你所接受的字符*/
            override fun getAcceptedChars(): CharArray {
                return digit.toCharArray()
            }
        }
    }
}