package com.hsicen.core.arch

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.hsicen.core.dagger.android.HasFragmentInjector
import dagger.Lazy
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * 作者：hsicen  2020/9/5 23:01
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：依赖注入Activity
 */
abstract class InjectedActivity(
  @LayoutRes private val layoutResID: Int
) : CoreActivity(layoutResID), HasFragmentInjector {

  /** Fragment注入器. */
  @Inject
  protected lateinit var frgInjector: Lazy<DispatchingAndroidInjector<Fragment>>

  override fun fragmentInjector(): AndroidInjector<Fragment> = frgInjector.get()

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    inject()
  }

  /*** 加入编织图.*/
  abstract fun inject()

}