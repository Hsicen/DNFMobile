package com.hsicen.core.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 作者：hsicen  2020/9/6 23:07
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：SavedStateHandle扩展
 */

/**
 * 代理特定key的值.
 * @receiver SavedStateHandle
 * @param key String?
 * @return ReadWriteProperty<Any, T?>
 */
inline fun <reified T> SavedStateHandle.delegate(key: String? = null): ReadWriteProperty<Any, T?> =
  object : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? {
      val stateKey = key ?: property.name
      return this@delegate[stateKey]
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
      val stateKey = key ?: property.name
      this@delegate[stateKey] = value
    }
  }

/**
 * 获取特定key的LiveData对象.
 * 原始方式 SavedStateHandle.getLiveData
 * @receiver SavedStateHandle
 * @param key String?
 * @return ReadOnlyProperty<Any, MutableLiveData<T?>>
 */
inline fun <reified T> SavedStateHandle.livedata(key: String? = null) =
  ReadOnlyProperty<Any, MutableLiveData<T?>> { _, property ->
    val stateKey = key ?: property.name
    this@livedata.getLiveData(stateKey)
  }
