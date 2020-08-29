package com.hsicen.core.utils.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * 作者：hsicen  2020/8/29 22:51
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：gson类型转换兼容处理
 */
class FloatAdapter : JsonSerializer<Float>, JsonDeserializer<Float> {

  override fun serialize(src: Float?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement = JsonPrimitive(src)

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Float {
    if (json is JsonNull || json?.asString == "" || json?.asString == "null") {
      return 0f
    }
    return json?.asFloat ?: 0f
  }
}