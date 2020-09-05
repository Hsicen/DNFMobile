package com.hsicen.core.exceptions

/**
 * 作者：hsicen  2020/8/10 22:42
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：自定义异常
 */
class KException(message: String, val errCode: Int = -1, cause: Throwable? = null) :
  RuntimeException(message, cause) {

  object Code {
    /** 类型转换异常  服务端返回了Null. */
    const val TYPE_CONVERSION_EXCEPTION = -0x10001
  }
}