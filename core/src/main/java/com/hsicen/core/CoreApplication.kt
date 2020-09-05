package com.hsicen.core

import android.app.Activity
import android.content.pm.ApplicationInfo
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDexApplication
import com.hsicen.core.dagger.android.HasActivityInjector
import com.hsicen.core.dagger.android.HasFragmentInjector
import com.hsicen.core.dagger.comoonent.DaggerCoreComponent
import com.hsicen.core.dagger.module.CoreDataModule
import dagger.Lazy
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import leakcanary.AppWatcher
import leakcanary.ObjectWatcher
import javax.inject.Inject

/**
 * 作者：hsicen  2020/8/22 12:07
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：Application 核心配置
 *
 * 支持MultiDex.
 */
open class CoreApplication : MultiDexApplication(), HasActivityInjector, HasFragmentInjector {

  /** Activity注入器. */
  @Inject
  lateinit var actInjector: Lazy<DispatchingAndroidInjector<Activity>>

  /** Fragment注入器. */
  @Inject
  lateinit var frgInjector: Lazy<DispatchingAndroidInjector<Fragment>>

  /** 对象引用监测器. */
  protected var refWatcher: ObjectWatcher? = null

  /** 调试模式是否开启. */
  protected val isDebug: Boolean by lazy(LazyThreadSafetyMode.NONE) { isDebugMode() }

  /** 核心组件. */
  protected val coreComponent by lazy {
    DaggerCoreComponent.builder()
      .seedInstance(this)
      .coreDataModule(CoreDataModule(refWatcher))
      .build()
  }

  override fun activityInjector(): AndroidInjector<Activity> = actInjector.get()

  override fun fragmentInjector(): AndroidInjector<Fragment> = frgInjector.get()

  /**
   * 是否是debug模式.
   * @return Boolean
   */
  private fun isDebugMode(): Boolean =
    runCatching {
      val info = applicationInfo
      info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }.getOrNull() ?: false

  /*** 安装内存泄漏监测.*/
  private fun installLeakCanary() {
    refWatcher = AppWatcher.objectWatcher
  }

}