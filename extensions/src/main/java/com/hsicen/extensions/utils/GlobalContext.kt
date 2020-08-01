package com.hsicen.extensions.utils

import android.content.Context

/**
 * 作者：hsicen  2020/8/1 22:55
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：提供全局Context
 */
object GlobalContext {
    /** 全局context. */
    internal lateinit var mContext: Context

    /*** 获取全局context*/
    fun getContext(): Context = mContext
}

fun getContext(): Context {
    return GlobalContext.getContext()
}