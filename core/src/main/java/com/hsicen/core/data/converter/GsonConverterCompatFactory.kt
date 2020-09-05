package com.hsicen.core.data.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 作者：hsicen  2020/9/5 17:55
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：DNFMobile
 */
class GsonConverterCompatFactory(val gson: Gson) : Converter.Factory() {

  override fun responseBodyConverter(
    type: Type, annotations: Array<Annotation>,
    retrofit: Retrofit
  ): Converter<ResponseBody, *> {

    val adapter = gson.getAdapter(TypeToken.get(type))
    return GsonResponseBodyCompatConverter(adapter)
  }


  override fun requestBodyConverter(
    type: Type, parameterAnnotations: Array<Annotation>,
    methodAnnotations: Array<Annotation>, retrofit: Retrofit
  ): Converter<*, RequestBody>? {

    val adapter = gson.getAdapter(TypeToken.get(type))
    return GsonRequestBodyCompatConverter(gson, adapter)
  }

}