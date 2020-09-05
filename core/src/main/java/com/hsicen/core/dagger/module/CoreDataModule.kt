package com.hsicen.core.dagger.module

import android.content.Context
import androidx.annotation.Nullable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hsicen.core.CoreApplication
import com.hsicen.core.data.converter.NullStringToEmptyGsonTypeAdapterFactory
import com.hsicen.core.utils.ActivityManager
import com.hsicen.core.utils.FileManager
import com.hsicen.core.utils.gson.*
import dagger.Module
import dagger.Provides
import leakcanary.ObjectWatcher
import javax.inject.Singleton

/**
 * 作者：hsicen  2020/9/1 22:13
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：核心数据模块
 */

@Module(
  includes = [
    NetModule::class,
    DatabaseModule::class
  ]
)
class CoreDataModule(private val refWatcher: ObjectWatcher?) {

  /**
   * 对象引用监测器.
   * @return RefWatcher?
   */
  @Nullable
  @Provides
  fun refWatcher(): ObjectWatcher? = refWatcher

  /**
   * Activity管理.
   * @param ctx Context
   * @return KActivityManager
   */
  @Singleton
  @Provides
  fun activityManager(ctx: Context): ActivityManager = ActivityManager(ctx)

  /**
   * 提供Context实例.
   * @param application Application
   * @return Context
   */
  @Provides
  fun provideContext(application: CoreApplication): Context = application

  /**
   * 提供gson实例.
   * @return Gson
   */
  @Provides
  fun provideGson(): Gson = GsonBuilder()
    .registerTypeAdapterFactory(NullStringToEmptyGsonTypeAdapterFactory())
    .registerTypeAdapter(Int::class.java, IntAdapter())
    .registerTypeAdapter(Float::class.java, FloatAdapter())
    .registerTypeAdapter(Double::class.java, DoubleAdapter())
    .registerTypeAdapter(Boolean::class.java, BooleanAdapter())
    .registerTypeAdapter(Long::class.java, LongAdapter())
    .create()

  /**
   * 提供全局文件管理系统.
   * @param context Context
   * @return KFileManager
   */
  @Provides
  @Singleton
  fun provideFileManager(context: Context) = FileManager(context)
}
