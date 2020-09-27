package com.hsicen.extensions.extensions

import java.lang.Math as nativeMath

/**
 * 作者：hsicen  2020/9/20 18:57
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：Number扩展
 */

inline fun Int.pow(x: Int): Int = nativeMath.pow(this.toDouble(), x.toDouble()).toInt()