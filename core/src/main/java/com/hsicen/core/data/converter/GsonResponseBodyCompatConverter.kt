@file:Suppress("UNCHECKED_CAST")

package com.hsicen.core.data.converter

import com.google.gson.TypeAdapter
import com.hsicen.core.data.Response
import com.hsicen.core.data.ServerCodes
import com.hsicen.core.data.TokenExpired
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter

/**
 * 作者：hsicen  2020/9/5 17:41
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：ResponseBody 处理
 */
class GsonResponseBodyCompatConverter<T>(
  private val adapter: TypeAdapter<T>
) : Converter<ResponseBody, T> {

  override fun convert(value: ResponseBody): T? {
    try {
      value.use {
        // 注意：body数据只能读取一次
        val jsonBody = value.string()
        val jsonObj = JSONObject(jsonBody)
        val code = jsonObj.optInt("code")
        val msg = jsonObj.optString("msg", jsonObj.optString("message"))
        // 判断接口是否请求异常
        when (code) {
          ServerCodes.SUCCESS -> {
            val dataJson = jsonObj.get("data").toString()
            // 判断data是否为null
            if (dataJson.isBlank() || dataJson == "null" || dataJson == "(null)") {
              return Response(code, Any(), msg) as T
            }
            return adapter.fromJson(jsonBody)
          }

          ServerCodes.TOKEN_EXPIRED -> {
            TokenExpired.updateTokenExpired(true)
            throw Response.Exception(Response(code, "", msg))
          }

          else -> throw Response.Exception(Response(code, "", msg))
        }
      }
    } catch (e: JSONException) {
      throw RuntimeException(e)
    }
  }
}
