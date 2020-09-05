package com.hsicen.core

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.chibatching.kotpref.Kotpref
import com.hsicen.core.arouter.ARouterApi
import com.hsicen.core.dagger.android.HasActivityInjector
import com.hsicen.core.dagger.android.HasFragmentInjector
import com.hsicen.core.dagger.comoonent.DaggerCoreComponent
import com.hsicen.core.dagger.module.CoreDataModule
import com.hsicen.core.data.io
import com.hsicen.extensions.extensions.yes
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
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

  override fun onCreate() {
    super.onCreate()

    isMainProcess().yes {
      initARouter()
      init()
    }
  }

  @CallSuper
  open fun init() {
    installLeakCanary()
    coreComponent.inject(this)
    coreComponent.activityManager().register(this)

    //异步初始化
    io { asyncInit() }
  }

  override fun onTerminate() {
    super.onTerminate()

    ARouter.getInstance().destroy()
  }

  /*** 异步初始化.*/
  @CallSuper
  protected open suspend fun asyncInit() {
    initLogger()
    initKotpref()
  }

  /*** 初始化日志*/
  private fun initLogger() {
    Logger.addLogAdapter(object : AndroidLogAdapter() {
      override fun isLoggable(priority: Int, tag: String?): Boolean = isDebug
    })
  }

  /*** 初始化kotpref*/
  private fun initKotpref() {
    Kotpref.init(this)
  }

  /*** 安装内存泄漏监测.*/
  private fun installLeakCanary() {
    refWatcher = AppWatcher.objectWatcher
  }

  /*** 初始化路由.*/
  private fun initARouter() {
    ARouterApi.init(this, isDebug)
  }

  /*** 是否是debug模式.*/
  private fun isDebugMode(): Boolean =
    runCatching {
      val info = applicationInfo
      info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }.getOrNull() ?: false

  override fun activityInjector(): AndroidInjector<Activity> = actInjector.get()

  override fun fragmentInjector(): AndroidInjector<Fragment> = frgInjector.get()

  /**
   * 内存回收的时候清除glide对应等级的内存
   * @param level TRIM_MEMORY_UI_HIDDEN表示应用程序的所有UI界面被隐藏了，
   * 即用户点击了Home键或者Back键导致应用的UI界面不可见．这时候应该释放一些资源
   */
  override fun onTrimMemory(level: Int) {
    super.onTrimMemory(level)

    if (level == TRIM_MEMORY_UI_HIDDEN) {
      Glide.get(this).clearMemory()
    }
    Glide.get(this).trimMemory(level)
  }

  /*** 低内存时相关处理*/
  override fun onLowMemory() {
    super.onLowMemory()

    Glide.get(this).clearMemory()
  }

  open fun isMainProcess(): Boolean = true

  companion object {
    /**
     * 获取核心组件实例.
     * @param context Context
     * @return CoreComponent
     */
    @JvmStatic
    fun coreComponent(context: Context) =
      (context.applicationContext as CoreApplication).coreComponent
  }
}

/**
 * Context获取核心组件扩展.
 * @receiver Context
 * @return CoreComponent
 */
fun Context.coreComponent() = CoreApplication.coreComponent(this)

/**
 * Activity获取核心组件扩展.
 * @receiver Activity
 * @return CoreComponent
 */
fun Activity.coreComponent() = CoreApplication.coreComponent(this)
