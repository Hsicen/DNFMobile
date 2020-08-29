@file:Suppress("UNCHECKED_CAST")

package com.hsicen.core.data.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * 作者：hsicen  2020/8/29 22:55
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：json中的null处理
 */
class NullStringToEmptyGsonTypeAdapterFactory : TypeAdapterFactory {

  override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
    val rawType = type?.rawType as Class<T>
    return if (rawType != String::class.java) {
      null
    } else StringNullAdapter() as TypeAdapter<T>
  }

  class StringNullAdapter : TypeAdapter<String>() {
    override fun write(out: JsonWriter?, value: String?) {
      if (value == null) {
        out?.nullValue()
        return
      }
      out?.value(value)
    }

    override fun read(source: JsonReader?): String? {
      if (source?.peek() == JsonToken.NULL) {
        source.nextNull()
        return ""
      }
      return source?.nextString()
    }
  }
}