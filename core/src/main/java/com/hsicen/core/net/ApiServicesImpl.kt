package com.hsicen.core.net

import retrofit2.Retrofit
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * 作者：hsicen  2020/8/27 22:58
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：这里做了对api服务的缓存处理
 */
@Suppress("UNCHECKED_CAST")
class ApiServicesImpl(private val retrofit: Retrofit) : ApiServices {

  /** api缓存. */
  private val apiServiceCache = ConcurrentHashMap<KClass<out Any>, Any>()

  override fun <T : Any> create2(api: KClass<T>): T {
    var result = apiServiceCache[api]
    if (result != null) return result as T

    synchronized(apiServiceCache) {
      result = apiServiceCache[api]
      if (result == null) {
        result = retrofit.create(api.java).also {
          apiServiceCache[api] = it
        }
      }
    }
    return result as T
  }
}