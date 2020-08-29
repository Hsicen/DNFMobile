package com.hsicen.core.dagger.android

import android.app.Activity
import dagger.android.AndroidInjector

/**
 * 作者：hsicen  2020/8/27 22:37
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：是否有Activity注入器
 */
interface HasActivityInjector {

  /**
   * 获取注入器.
   * @return AndroidInjector<Activity>
   */
  fun activityInjector(): AndroidInjector<Activity>
}