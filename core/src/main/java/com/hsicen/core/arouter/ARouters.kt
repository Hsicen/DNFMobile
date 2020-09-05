package com.hsicen.core.arouter

import android.app.Activity
import android.app.Application
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.hsicen.core.BuildConfig
import com.hsicen.core.data.Codes
import com.hsicen.core.utils.toast.debugInfo
import com.orhanobut.logger.Logger

/**
 * 作者：hsicen  2020/9/1 22:52
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：这里存放所有ARouter的跳转Path
 *
 * 动态跳转功能：根据服务器返回的Code进行跳转
 */
object ARouters {

  /*** 主页面*/
  object Main {
    const val MAIN = "/main/main"
    const val MINE = "/main/mine"

    /**
     * 获取后台接口地址
     * @return string
     */
    fun getHttpServerHost(): String {
      return BuildConfig.HTTP_SERVER_URL
    }
  }

  /**
   * 通过code返回路径,此处为原生界面的code则返回了对应界面路由地址，web或者其他操作提示返回空
   * @param code 原生页面跳转Code
   */
  fun getPathByCode(code: String?): String? {
    code ?: return null

    return when (code) {
      Codes.MAIN -> Main.MAIN
      Codes.MINE -> Main.MINE
      else -> null
    }
  }
}

/**
 * 路由导航.
 * @receiver String
 * @param config ((Postcard) -> Unit)?
 */
fun String.navigation(
  activity: Activity? = null,
  requestCode: Int? = null,
  callback: NavigationCallback? = null,
  config: (Postcard.() -> Unit)? = null
) {
  runCatching {
    ARouter.getInstance()
      .build(this).apply {
        config?.invoke(this)
      }
      .apply {
        if (requestCode != null && activity != null) {
          navigation(activity, requestCode, callback)
        } else {
          navigation(activity, callback)
        }
      }
  }.onFailure {
    Logger.e(it, "")
    debugInfo("页面跳转失败")
  }
}

/**
 * 获取服务.
 * @return T?
 */
inline fun <reified T : IProvider> getAService(): T? =
  ARouter.getInstance().navigation(T::class.java)

object ARouterApi {
  /**
   * ARouter相关初始化.
   * @param application Application
   * @param debuggable Boolean
   */
  fun init(application: Application, debuggable: Boolean) {
    if (debuggable) {
      ARouter.openDebug()
      ARouter.openLog()
    }
    ARouter.init(application)
  }
}
