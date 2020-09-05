package com.hsicen.core.arch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hsicen.core.dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * 作者：hsicen  2020/9/5 23:03
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：依赖注入DialogFragment
 */
abstract class InjectedDialogFragment : CoreDialogFragment() {

  @Inject
  lateinit var ctx: Context

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState == null) {
      inject()
    }
  }

  /*** 可重写该方法自行注入.*/
  protected open fun inject() {
    //加入编织图
    AndroidInjection.inject(this)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    if (savedInstanceState != null) {
      AndroidInjection.inject(this)
    }
    return super.onCreateView(inflater, container, savedInstanceState)
  }
}
