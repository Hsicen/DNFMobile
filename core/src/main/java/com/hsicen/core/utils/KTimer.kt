package com.hsicen.core.utils

import android.os.Handler
import android.os.Looper

/**
 * 作者：hsicen  2020/9/6 23:03
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：计时器
 */
class KTimer(val interval: Long, val call: (() -> Unit)? = null) {
  private val runnable = object : Runnable {
    override fun run() {
      call?.invoke()
      MAIN_HANDLER.removeCallbacks(this)
      MAIN_HANDLER.postDelayed(this, interval)
    }
  }

  fun start() {
    stop()
    MAIN_HANDLER.postDelayed(runnable, interval)
  }

  fun stop() {
    MAIN_HANDLER.removeCallbacks(runnable)
  }

  companion object {
    val MAIN_HANDLER = Handler(Looper.getMainLooper())
  }
}
