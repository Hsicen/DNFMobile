package com.hsicen.core.utils

import android.content.Context
import com.hsicen.core.GlobalConfigs
import com.hsicen.core.data.safetyIO
import com.hsicen.core.data.sp.KSpCache
import com.hsicen.extensions.extensions.*
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 作者：hsicen  2020/8/30 14:32
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：手机imei管理(唯一识别码)
 */
@Singleton
class KImei @Inject constructor(private val ctx: Context) {

  private var _imei: String = ""
    get() {
      // 如果为空 读去手机imei
      if (field.isEmpty()) {
        field = ctx.imei ?: ""
        // 从sp缓存中读取
        if (field.isEmpty()) {
          field = readCacheImei()
        } else {
          KSpCache.imei = String(field.aesEncrypt(GlobalConfigs.AES_KEY).toBase64())
          cacheImei(field)
        }
      }
      return field.replace("-", "")
    }

  /** 手机imei值.*/
  val imei: String
    get() = _imei

  /**
   * 读取缓存imei.
   * @return String
   */
  private fun readCacheImei(): String =
    runCatching {
      var imei = String(KSpCache.imei.base64Decode().aesDecrypt(GlobalConfigs.AES_KEY))
      // 读取文件缓存.
      if (imei.isEmpty()) {
        imei = runCatching {
          File(ctx.filesDir, IMEI_FILE_NAME).readText()
        }.getOrNull() ?: ""
        if (imei.isEmpty()) {
          // 生成随机imei
          imei = UUID.randomUUID().toString().replace("-", "").toMd5().substring(8, 24)
          KSpCache.imei = String(imei.aesEncrypt(GlobalConfigs.AES_KEY).toBase64())
          cacheImei(imei)
        }
      } else {
        cacheImei(imei)
      }
      imei
    }.getOrNull() ?: ""

  /**
   * 缓存imei.
   * @param imei String
   */
  private fun cacheImei(imei: String) {
    safetyIO {
      File(ctx.filesDir, IMEI_FILE_NAME).writeText(imei)
    }
  }

  companion object {
    private const val IMEI_FILE_NAME = "kawqiuqi"
  }
}
