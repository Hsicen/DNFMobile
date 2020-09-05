package com.hsicen.core.net

import kotlin.reflect.KClass

/**
 * 作者：hsicen  2020/8/27 22:57
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Retrofit扩展
 */
interface ApiServices {

  /**
   * 创建api接口.
   * @param api KClass<T>
   * @return T
   */
  fun <T : Any> create2(api: KClass<T>): T
}

inline fun <reified T : Any> ApiServices.create(): T = create2(T::class)