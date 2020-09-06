package com.hsicen.core.utils.cache

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.hsicen.core.R
import com.hsicen.core.coreComponent
import com.hsicen.core.exceptions.KException
import com.hsicen.core.exceptions.handleForNetwork
import com.hsicen.core.utils.gson.toJson
import com.hsicen.core.utils.gson.toObj
import com.hsicen.core.utils.toast.info
import com.hsicen.extensions.extensions.networkAvailable
import com.hsicen.extensions.extensions.no
import java.util.concurrent.CancellationException

/**
 * 作者：hsicen  2020/9/6 15:14
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：数据库帮助类
 */
class DbCacheHelper<T : Any>(
  private val ctx: Context,
  private val mode: Int = MODE_ONCE,
  private val typeToken: TypeToken<T>
) {

  val dbCache by lazy { ctx.coreComponent().dbCache() }

  /** 读取缓存次数. */
  private var readCacheTimes = 0

  /** 当前计数的Cache key. */
  private var curCacheKey = "###"

  /**
   *
   * 执行.
   * @param cacheEnable Boolean
   * @param cacheKey String
   * @param userId Int 默认未登录是为-1
   * @param noCacheTriggerFailure Boolean
   * @param hasCacheTriggerActionFailure Boolean
   * @param onSuccess Function2<[@kotlin.ParameterName] T, [@kotlin.ParameterName] Boolean, Unit>?
   * @param onFailure Function2<[@kotlin.ParameterName] Throwable, [@kotlin.ParameterName] Boolean, Unit>?
   * @param action SuspendFunction0<Result<T>>
   */
  suspend fun execute(
    cacheEnable: Boolean = true,
    cacheKey: String,
    userId: Int = -1,
    noCacheTriggerFailure: Boolean = false,
    hasCacheTriggerActionFailure: Boolean = true,
    noNetCacheCallBack: Boolean = true,
    noticeUpdate: Boolean = true,
    onSuccess: ((data: T, fromCache: Boolean) -> Unit)? = null,
    onFailure: ((t: Throwable, fromCache: Boolean) -> Unit)? = null,
    action: suspend () -> Result<T>
  ) {
    // 如果key变化 则直接重置次数
    if (cacheKey != curCacheKey) {
      readCacheTimes = 0
    }
    curCacheKey = cacheKey
    var hasCache = false
    if (cacheEnable && (mode != MODE_ONCE || readCacheTimes < 1)) {
      // 没有网才读取缓存
      if (noNetCacheCallBack) {
        // 读取缓存.
        val cacheData = dbCache.get(cacheKey, userId)?.value?.toObj<T>(typeToken.type)
        if (cacheData != null) {
          hasCache = true
          onSuccess?.invoke(cacheData, true)
        } else if (noCacheTriggerFailure) {
          onFailure?.invoke(KException("no cache for cache-key: $cacheKey"), true)
        }
        readCacheTimes++
      } else {
        ctx.networkAvailable.no {
          // 读取缓存.
          val cacheData = dbCache.get(cacheKey, userId)?.value?.toObj<T>(typeToken.type)
          if (cacheData != null) {
            hasCache = true
            onSuccess?.invoke(cacheData, true)
          } else if (noCacheTriggerFailure) {
            onFailure?.invoke(KException("no cache for cache-key: $cacheKey"), true)
          }
          readCacheTimes++
        }
      }

    }
    action.invoke()
      .onSuccess {
        // 缓存结果
        dbCache.set(
          cacheKey,
          it.toJson(),
          userId
        )
        onSuccess?.invoke(it, false)
      }
      .onFailure {
        // 这里判断下是否是服务端返回null 导致类型转换异常 null表示服务端没有数据 所有需要移除缓存
        if (it is KException && it.errCode == KException.Code.TYPE_CONVERSION_EXCEPTION) {
          dbCache.remove(cacheKey)
        }
        if (noticeUpdate) {
          it.handleForNetwork()
        } else {
          if (it !is CancellationException) {
            info(R.string.error_not_network)
          }
        }
        if ((hasCache && hasCacheTriggerActionFailure) || !hasCache) {
          onFailure?.invoke(it, false)
        }
      }
  }

  companion object {
    /** 只读一次缓存. */
    const val MODE_ONCE = 0x01

    /** 每次都读缓存. */
    const val MODE_ALL = 0x02
  }
}
