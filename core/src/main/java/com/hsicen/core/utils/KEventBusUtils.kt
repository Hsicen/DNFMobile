package com.hsicen.core.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.Observable

/**
 * 作者：hsicen  2020/9/6 17:35
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：事件监听
 */

inline fun <reified T> liveEventBus(): Observable<Any> =
  LiveEventBus.get(T::class.java.canonicalName!!)

/**
 * 订阅事件.
 * @receiver Fragment
 * @param onEvent (T) -> Unit
 */
inline fun <reified T> Fragment.observeLiveEvent(crossinline onEvent: (T) -> Unit) =
  liveEventBus<T>()
    .observe(this, Observer<Any> { onEvent.invoke(it as T) })

/**
 * 订阅事件.
 * @receiver FragmentActivity
 * @param onEvent (T) -> Unit
 */
inline fun <reified T> FragmentActivity.observeLiveEvent(crossinline onEvent: (T) -> Unit) =
  liveEventBus<T>()
    .observe(this, Observer<Any> { onEvent.invoke(it as T) })

/**
 * 订阅事件，粘性.
 * @receiver Fragment
 * @param onEvent (T) -> Unit
 */
inline fun <reified T> Fragment.observeStickyLiveEvent(crossinline onEvent: (T) -> Unit) =
  liveEventBus<T>()
    .observeSticky(this, Observer<Any> { onEvent.invoke(it as T) })

/**
 * 订阅事件，粘性.
 * @receiver FragmentActivity
 * @param onEvent (T) -> Unit
 */
inline fun <reified T> FragmentActivity.observeStickyLiveEvent(crossinline onEvent: (T) -> Unit) =
  liveEventBus<T>()
    .observeSticky(this, Observer<Any> { onEvent.invoke(it as T) })