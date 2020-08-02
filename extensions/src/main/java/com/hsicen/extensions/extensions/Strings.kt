package com.hsicen.extensions.extensions

/**
 * 作者：hsicen  2020/8/2 15:25
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：String相关扩展
 */

/**
 * 计算字节，中午算2个.
 * @receiver String
 * @return Int
 */
fun String.counterChars(): Int {
    if (isBlank()) return 0
    var count = 0
    toCharArray().forEach {
        count += if (it.toInt() in 1..126) {
            1
        } else {
            2
        }
    }
    return count
}
