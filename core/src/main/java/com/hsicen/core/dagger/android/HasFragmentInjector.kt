package com.hsicen.core.dagger.android

import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector

/**
 * 作者：hsicen  2020/8/27 22:35
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：是否有Fragment注入器
 */
interface HasFragmentInjector {

  /**
   * 获取注入器.
   * @return AndroidInjector<Fragment>
   */
  fun fragmentInjector(): AndroidInjector<Fragment>
}