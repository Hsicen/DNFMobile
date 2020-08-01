package com.hsicen.extensions.utils

import androidx.annotation.Keep
import androidx.core.content.FileProvider

/**
 * 作者：hsicen  2020/8/1 22:54
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：通过Provider提供全局Context
 */
@Keep
class CustomProvider : FileProvider() {

    override fun onCreate(): Boolean {
        GlobalContext.mContext = context!!
        return super.onCreate()
    }
}