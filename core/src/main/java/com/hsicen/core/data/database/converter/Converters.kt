package com.hsicen.core.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.hsicen.core.utils.gson.toJson
import com.hsicen.core.utils.gson.toObj

/**
 * 作者：hsicen  2020/8/29 22:46
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：类型转换器
 */
class Converters {

  @TypeConverter
  fun stringListFromString(text: String?): List<String>? {
    return text?.toObj(object : TypeToken<List<String>>() {}.type)
  }

  @TypeConverter
  fun stringFromStringList(list: List<String>?): String? {
    return list?.toJson()
  }
}
