package com.hsicen.core.net

import android.content.Context
import android.os.Build
import com.hsicen.core.arouter.ARouters
import com.hsicen.core.utils.KImei
import com.hsicen.extensions.extensions.screenHeight
import com.hsicen.extensions.extensions.screenWidth
import com.hsicen.extensions.extensions.toHexString
import com.hsicen.extensions.extensions.versionName
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

/**
 * 作者：hsicen  2020/9/1 22:48
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Http请求拦截器，主要用于给请求连接添加Header
 */
class HttpHeadersInterceptor(
  private val ctx: Context,
  private val kImei: KImei
) : Interceptor {

  /** 代理. */
  private val userAgent: String by lazy {
    arrayOf(
      "MOBILE", // type
      "Android", // os
      "${Build.VERSION.SDK_INT}", // os_version
      ctx.versionName ?: "null", // 应用版本号
      Build.BRAND ?: "null", // os
      Build.MODEL ?: "null", // 手机型号
      "${ctx.screenWidth}x${ctx.screenHeight}", // 屏幕分辨率
      kImei.imei // 设备唯一标识
    ).joinToString(separator = "|") { it }
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    val oldRequest = chain.request()
    val isSelfHost = ARouters.Main.getHttpServerHost().contains(oldRequest.url.host)
    val newRequest = oldRequest.newBuilder()
      .addHeader(USER_AGENT, userAgent)
      .addHeader(X_TRACE_ID, UUID.randomUUID().toString().toByteArray().toHexString())
      .addHeader(UDID, kImei.imei)
      .addHeader(CONNECTION, "close")
      .url(oldRequest.url.newBuilder()
        .apply {
          if (isSelfHost) {
            addEncodedPathSegment("$$$$$")
            val newPathSegment = oldRequest.url.pathSegments.toMutableList().apply {
              add(0, "api")
            }
            var isNeedEmpty = false
            if (newPathSegment.lastOrNull()?.isEmpty() == true) {
              newPathSegment.removeAt(newPathSegment.lastIndex)
              isNeedEmpty = true
            }
            newPathSegment.forEachIndexed { index, s ->
              setPathSegment(index, s)
            }
            if (isNeedEmpty) {
              addPathSegment("")
            }
          }
        }
        .build())
      .build()
    return chain.proceed(newRequest)
  }

  companion object {
    /** 代理名. */
    private const val USER_AGENT = "User-Agent"

    /** 链路追踪id. */
    private const val X_TRACE_ID = "X-TRACE-ID"

    /** 设备udid(取设备唯一标识). */
    private const val UDID = "Udid"

    /** 发起请求返回结果就断掉. */
    private const val CONNECTION = "Connection"
  }
}
