package com.hsicen.extensions.extensions

/**
 * 作者：hsicen  2020/8/2 15:10
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：数据格式化、转换相关扩展.
 */

/** HEX字符集. */
private val HEX_DIGITS =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

/**
 * 字节转16进制字符串.
 * @receiver Byte
 * @return String
 */
fun Byte.toHexString(): String =
    if (Integer.toHexString(0xff and this.toInt()).length == 1) {
        "0" + Integer.toHexString(0xff and this.toInt())
    } else {
        Integer.toHexString(0xff and this.toInt()).toString()
    }

/**
 * 字节数组转16进制字符串.
 * @receiver ByteArray
 * @return String
 */
fun ByteArray.toHexString(): String =
    StringBuffer().also {
        for (i in indices) {
            if (Integer.toHexString(0xff and this[i].toInt()).length == 1) {
                it.append("0").append(Integer.toHexString(0xff and this[i].toInt()))
            } else {
                it.append(Integer.toHexString(0xff and this[i].toInt()))
            }
        }
    }.toString()

/**
 * 16进制字符串转字节数组.
 * @receiver String
 * @return ByteArray
 */
fun String.hexStringToByteArray(): ByteArray {
    val data = ByteArray(length / 2)
    var i = 0
    while (i < length) {
        data[i / 2] =
            ((Character.digit(get(i), 16) shl 4) + Character.digit(get(i + 1), 16)).toByte()
        i += 2
    }
    return data
}

/**
 * 字节数据转16进制字符，该字符串会被格式化.
 * [xx, xxx, xx]
 * @receiver ByteArray
 * @return String
 */
fun ByteArray.toHexStringFormat(): String =
    StringBuffer().also {
        var firstEntry = true
        it.append('[')

        for (b in this) {
            if (!firstEntry) {
                it.append(", ")
            }
            it.append(HEX_DIGITS[b.toInt() and 0xF0 shr 4])
            it.append(HEX_DIGITS[b.toInt() and 0x0F])
            firstEntry = false
        }

        it.append(']')
    }.toString()