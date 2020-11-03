package com.hsicen.core.data.sp

import com.chibatching.kotpref.KotprefModel

/**
 * 作者：hsicen  2020/8/30 16:29
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：全局Sp缓存
 */
object KSpCache : KotprefModel() {

    /** 重定义Sp文件名字. */
    override val kotprefName: String
        get() = "hsicen_sp_cache"

    /** imei缓存. */
    var imei: String by stringPref("hsicen", "")
}
