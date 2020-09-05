package com.hsicen.core.data

import androidx.lifecycle.MutableLiveData

/**
 * 作者：hsicen  2020/9/5 17:47
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Token失效处理
 */
object TokenExpired {

  val status by lazy(LazyThreadSafetyMode.NONE) {
    MutableLiveData<Boolean>().apply {
      value = false
    }
  }

  @Synchronized
  fun updateTokenExpired(status: Boolean) {
    if (this.status.value == status) {
      return
    }
    this.status.postValue(status)
  }
}
