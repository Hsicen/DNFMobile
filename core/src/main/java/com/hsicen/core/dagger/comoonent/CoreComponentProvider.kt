package com.hsicen.core.dagger.comoonent

import android.content.Context
import com.google.gson.Gson
import com.hsicen.core.data.cache.DbCache
import com.hsicen.core.data.database.CoreDatabase
import com.hsicen.core.net.ApiServices
import com.hsicen.core.utils.*
import leakcanary.ObjectWatcher
import java.net.CookieStore

/**
 * 作者：hsicen  2020/8/27 22:53
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：对外提供核心功能的获取入口
 */
interface CoreComponentProvider {

  /**
   * 对外提供context实例获取入口.
   * @return Context
   */
  fun context(): Context

  /**
   * 对外提供RefWatcher实例获取入口.
   * @return RefWatcher?
   */
  fun refWatcher(): ObjectWatcher?

  /**
   * 对外提供gson实例获取入口.
   * @return Gson
   */
  fun gson(): Gson

  /**
   * 对外提供Retrofit实例获取入口.
   * @return KApiService
   */
  fun retrofit(): ApiServices

  /**
   * 对外功能cookies存取实例获取入口.
   * @return CookieStore
   */
  fun cookieStore(): CookieStore

  /**
   * 对外提供CoreDatabase实例获取入口.
   * @return CoreDatabase
   */
  fun database(): CoreDatabase

  /**
   * 对外提供数据库缓存实例.
   * @return DbCache
   */
  fun dbCache(): DbCache

  /**
   * 对外提供文件下载器实例.
   * @return KDownloader
   */
  fun downloader(): Downloader

  /**
   * 对外提供文件上传器实例.
   * @return KUploader
   */
  fun uploader(): Uploader

  /**
   * 对外提供图片文件上传器实例.
   * @return ImageUploader
   */
  fun imageUploader(): ImageUploader

  /**
   * 对外提供文件管理实例.
   * @return FileManager
   */
  fun fileManager(): FileManager

  /**
   * 对外提供imei访问实例.
   * @return KImei
   */
  fun kimei(): KImei

  /**
   * 对外提供Activity管理实例.
   * @return ActivityManager
   */
  fun activityManager(): ActivityManager
}