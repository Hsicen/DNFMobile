package com.hsicen.core.utils.gson

import com.google.gson.*
import java.lang.reflect.Type

/**
 * 作者：hsicen  2020/8/29 22:53
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：gson类型转换兼容处理
 */
class BooleanAdapter : JsonSerializer<Boolean>, JsonDeserializer<Boolean> {

  override fun serialize(src: Boolean?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement = JsonPrimitive(src)

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Boolean {
    if (json is JsonNull || json?.asString == "" || json?.asString == "null") {
      return false
    }
    return json?.asBoolean ?: false
  }
}