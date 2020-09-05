package com.hsicen.core.arch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.hsicen.core.dagger.android.AndroidInjection
import com.hsicen.core.dagger.android.HasFragmentInjector
import dagger.Lazy
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

/**
 * 作者：hsicen  2020/9/5 23:10
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：依赖注入Fragment
 */
abstract class InjectedFragment : CoreFragment(), HasFragmentInjector {

  @Inject
  protected lateinit var ctx: Context

  /** Fragment注入器. */
  @Inject
  protected lateinit var frgInjector: Lazy<DispatchingAndroidInjector<Fragment>>

  override fun fragmentInjector(): AndroidInjector<Fragment> = frgInjector.get()

  @CallSuper
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    if (savedInstanceState != null) {
      inject()
    }
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState == null) {
      inject()
    }
  }

  /*** 可重写该方法自行注入.*/
  protected open fun inject() {
    AndroidInjection.inject(this)
  }

  override fun onLazyInit() {
  }
}
