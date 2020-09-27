package com.hsicen.extensions.extensions

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import com.hsicen.extensions.utils.LiveDataBroadcast

/**
 * 作者：hsicen  2020/8/2 15:00
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：广播功能扩展
 */

/**
 * 订阅广播.
 * @receiver Context
 * @param filters Array<out String> action过滤.
 * @return LiveData<Intent>
 */
fun Context.liveDataBroadcast(vararg filters: String): LiveData<Intent> =
  liveDataBroadcast(IntentFilter().apply { filters.forEach { addAction(it) } })

/**
 * 订阅广播.
 * @receiver Context
 * @param filters IntentFilter action过滤.
 * @return LiveData<Intent>
 */
fun Context.liveDataBroadcast(filters: IntentFilter): LiveData<Intent> =
  LiveDataBroadcast(this, filters)
