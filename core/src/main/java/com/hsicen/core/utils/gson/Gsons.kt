package com.hsicen.core.utils.gson

import android.content.Context
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hsicen.core.data.converter.NullStringToEmptyGsonTypeAdapterFactory
import com.orhanobut.logger.Logger
import java.lang.reflect.Type
import kotlin.reflect.KClass

/**
 * 作者：hsicen  2020/8/29 22:48
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Gson扩展
 */

/** gson实例. */
val localGson: Gson by lazy {

  GsonBuilder()
    .setExclusionStrategies(object : ExclusionStrategy {
      /**
       * 设置要过滤的属性
       */
      override fun shouldSkipField(attr: FieldAttributes): Boolean {
        return false
      }

      /**
       * 设置要过滤的类
       */
      override fun shouldSkipClass(clazz: Class<*>): Boolean {
        // 这里，如果返回true就表示此类要过滤，否则就输出
        return false
      }
    })
    .registerTypeAdapterFactory(NullStringToEmptyGsonTypeAdapterFactory())
    .registerTypeAdapter(Int::class.java, IntAdapter())
    .registerTypeAdapter(Float::class.java, FloatAdapter())
    .registerTypeAdapter(Double::class.java, DoubleAdapter())
    .registerTypeAdapter(Boolean::class.java, BooleanAdapter())
    .registerTypeAdapter(Long::class.java, LongAdapter())
    .create()
}


/**
 * 对象转json字符.
 * @receiver T
 * @return String
 */
fun <T> T.toJson(): String = localGson.toJson(this)


/**
 * json字符转对象.
 * @receiver String
 * @return T?
 */
inline fun <reified T> String.toObj(): T? =
  kotlin.runCatching {
    localGson.fromJson(this, T::class.java)
  }.onFailure {
    Logger.e(it, "json解析失败")
  }.getOrNull()


fun <T : Any> String.toObj(kClass: KClass<T>): T? =
  kotlin.runCatching {
    localGson.fromJson(this, kClass.java)
  }.onFailure {
    Logger.e(it, "json解析失败")
  }.getOrNull()


/**
 * 字符串转对象.
 * @receiver String
 * @param typeOfT Type
 * @return T?
 */
fun <T> String.toObj(typeOfT: Type): T? =
  runCatching {
    localGson.fromJson<T>(this, typeOfT)
  }.onFailure {
    Logger.e(it, "json解析失败")
  }.getOrNull()


/**
 * 从asset中读取json.
 * @receiver Context
 * @param fileName String
 * @return T?
 */
fun Context.readJsonFromAsset(fileName: String): String? =
  runCatching {
    assets.open(fileName).reader().readText()
  }.getOrNull()


/**
 * 从asset中读取json.
 * @receiver Context
 * @param fileName String
 * @param typeOfT Type
 * @return T?
 */
fun <T> Context.readJsonFromAsset(fileName: String, typeOfT: Type): T? =
  runCatching {
    assets.open(fileName).reader().readText().toObj<T>(typeOfT)
  }.getOrNull()
