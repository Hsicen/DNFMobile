package com.hsicen.core.utils

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * 作者：hsicen  2020/9/6 17:33
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：倒计时计时器
 */
class KCountDownTimer internal constructor(
  private val millisInFuture: Long,
  private val countDownInterval: Long = 1000L
) : MutableLiveData<Long>() {

  /** 计时器. */
  private var timer: CountDownTimer? = null

  /*** 开始计时.*/
  private fun startTimer() {
    if (value == 0L) return
    timer = object : CountDownTimer(value ?: millisInFuture, countDownInterval) {
      override fun onFinish() {
        value = 0L
      }

      override fun onTick(millisUntilFinished: Long) {
        value = millisUntilFinished
      }
    }
    timer?.start()
  }

  override fun onActive() {
    super.onActive()
    startTimer()
  }

  override fun onInactive() {
    super.onInactive()
    timer?.cancel()
  }
}

/**
 * 倒计时.
 * @receiver T
 * @param millisInFuture Long
 * @param countDownInterval Long
 * @return LiveData<Long>
 */
fun kCountDown(millisInFuture: Long, countDownInterval: Long = 1000L): LiveData<Long> =
  KCountDownTimer(millisInFuture, countDownInterval)
