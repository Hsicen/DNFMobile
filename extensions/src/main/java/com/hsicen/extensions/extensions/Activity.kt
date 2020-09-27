package com.hsicen.extensions.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.orhanobut.logger.Logger

/**
 * 作者：hsicen  2020/7/19 18:45
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：Activity扩展
 */

inline fun <reified T> toActivity(context: Context, block: Intent.() -> Unit) {
  val intent = Intent(context, T::class.java)
  intent.block()
  context.startActivity(intent)
}

/**
 * 状态栏颜色.
 */
var Activity.windowStatusBarColor: Int
  get() = kotlin.runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.statusBarColor
    } else {
      0
    }
  }.getOrNull() ?: 0
  set(value) {
    runCatching {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = value
      }
    }
  }

/**
 * 状态栏透明.
 * @receiver Activity
 */
fun Activity.transparentStatusBar() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.decorView.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.statusBarColor = Color.TRANSPARENT
  }
}

fun Activity.setAndroidNativeLightStatusBar(dark: Boolean) {
  val decor = window.decorView
  window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
  if (dark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    decor.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
  } else {
    decor.systemUiVisibility =
      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
  }
}

@SuppressLint("PrivateApi")
fun Activity.removeGrayColor() {
  runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) //去除半透明状态栏
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //全屏显示
      window.statusBarColor = Color.TRANSPARENT
      val decorViewClazz = Class.forName("com.android.internal.policy.DecorView")
      val field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor")
      field.isAccessible = true
      field.setInt(window.decorView, Color.TRANSPARENT)
    }
  }.onFailure {
    Logger.e("${it.message}")
  }
}

/*** if false, set icons and text color to white.*/
fun Window.lightStatusBar(isLight: Boolean) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    if (isLight) {
      clearFlags(
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
      )
      decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    } else {
      clearFlags(
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
      )
      decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    statusBarColor = Color.TRANSPARENT
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    statusBarColor = Color.BLACK
  }
}
