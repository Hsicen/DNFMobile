package com.hsicen.core.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.hsicen.core.R
import com.hsicen.core.utils.toast.info
import com.hsicen.extensions.extensions.networkAvailable
import com.hsicen.extensions.utils.GlobalContext
import com.orhanobut.logger.Logger

/**
 * 作者：hsicen  2020/9/6 23:04
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：网络监听回调
 */
class NetworkCallbackImpl(
  private val connectivityManager: ConnectivityManager?,
  var status: ((status: String) -> Unit)? = null
) : ConnectivityManager.NetworkCallback() {

  override fun onAvailable(network: Network) {
    super.onAvailable(network)
    Logger.v("网络已连接")
    updateNetworkStatus(connectivityManager?.getNetworkCapabilities(network), true)
  }

  override fun onLost(network: Network) {
    super.onLost(network)
    Logger.v("网络已断开")
    if (GlobalContext.getContext().networkAvailable) {
      status?.invoke("wifi")
    } else {
      updateNetworkStatus(connectivityManager?.getNetworkCapabilities(network), false)
    }
  }

  private fun updateNetworkStatus(
    networkCapabilities: NetworkCapabilities?,
    connect: Boolean = true
  ) {
    if (connect) {
      when {
        networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
          ?: false ->
          status?.invoke("cellular")
        networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
          ?: false ->
          status?.invoke("wifi")
        else -> status?.invoke("other")
      }
    } else {
      info(R.string.error_not_network1)
      status?.invoke("none")
    }
  }
}
