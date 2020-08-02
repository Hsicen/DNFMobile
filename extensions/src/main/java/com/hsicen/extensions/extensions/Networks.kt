package com.hsicen.extensions.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData

/**
 * 作者：hsicen  2020/8/2 15:20
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：网络相关扩展
 */

/**
 * 当前网络是否可用.
 */
val Context.networkAvailable: Boolean
    get() = runCatching {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.activeNetworkInfo?.isAvailable == true
    }.getOrNull() ?: false

/**
 * 订阅网络状态.
 * @receiver Context
 * @return LiveData<NetworkInfo.State>
 */
fun Context.liveDataNetworkState(): LiveData<NetworkInfo.State> =
    this.liveDataBroadcast("android.net.conn.CONNECTIVITY_CHANGE")
        .map {
            val connectivityManager =
                this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.activeNetworkInfo?.state ?: NetworkInfo.State.UNKNOWN
        }