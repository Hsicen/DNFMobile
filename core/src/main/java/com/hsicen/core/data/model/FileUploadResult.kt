package com.hsicen.core.data.model

import com.google.gson.annotations.SerializedName

/**
 * 作者：hsicen  2020/8/30 14:10
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：文件上传结果
 */
data class FileUploadResult(
  @SerializedName("name")
  val name: String = "",
  @SerializedName("absolute_url")
  val url: String = ""
)