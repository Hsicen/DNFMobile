package com.hsicen.core.data.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.OutputStreamWriter
import java.nio.charset.Charset

/**
 * 作者：hsicen  2020/9/5 17:12
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：RequestBody 处理
 */
class GsonRequestBodyCompatConverter<T>(
  private val gson: Gson,
  private val adapter: TypeAdapter<T>
) : Converter<T, RequestBody> {

  override fun convert(value: T): RequestBody? {
    val buffer = Buffer()
    val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
    val jsonWriter = gson.newJsonWriter(writer)
    adapter.write(jsonWriter, value)
    jsonWriter.close()
    return buffer.readByteString().toRequestBody(MEDIA_TYPE)
  }

  companion object {
    private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    private val UTF_8 = Charset.forName("UTF-8")
  }
}