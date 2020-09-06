package com.hsicen.core.utils

import java.util.concurrent.ConcurrentHashMap

/**
 * 作者：hsicen  2020/9/6 23:01
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：内存缓存
 */
object KMemoryCache {

  /** 缓存. */
  private val cacheMap = ConcurrentHashMap<String, Any>()

  /**
   * 添加，设置null表示移除.
   * @param key String
   * @param value Any?
   */
  @Synchronized
  fun set(key: String, value: Any?) {
    if (value == null) {
      cacheMap.remove(key)
    } else {
      cacheMap[key] = value
    }
  }

  fun <T> get(key: String): T? =
    runCatching {
      cacheMap[key] as? T
    }.getOrNull()

  /**
   * 清空缓存.
   */
  fun clear() {
    cacheMap.clear()
  }
}

/**
 * 添加到内存缓存中.
 * @receiver Any
 * @param key String
 */
operator fun Any?.plusAssign(key: String) {
  KMemoryCache.set(key, this)
}

/**
 * 获取内存缓存.
 * @receiver String
 * @return T?
 */
fun <T> String.getMemoryCache() = KMemoryCache.get<T>(this)
