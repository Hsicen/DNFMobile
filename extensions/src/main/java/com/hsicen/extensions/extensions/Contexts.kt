@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.hsicen.extensions.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.hsicen.extensions.R
import com.hsicen.extensions.utils.ComInternals
import com.hsicen.extensions.utils.ProcessUtil
import java.io.File

/**
 * 作者：hsicen  2020/8/2 15:02
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：上下文对象功能扩展
 */

val Context.appName
  get() = appName(packageName)

inline fun Context.appName(packageName: String) =
  packageManager.getPackageInfo(
    packageName,
    0
  )?.applicationInfo?.loadLabel(packageManager).toString()

val Context.appIcon
  get() = appIcon(packageName)

inline fun Context.appIcon(packageName: String) =
  packageManager.getPackageInfo(packageName, 0)?.applicationInfo?.loadIcon(packageManager)

val Context.versionName
  get() = versionName(packageName)

inline fun Context.versionName(packageName: String) =
  packageManager.getPackageInfo(packageName, 0)?.versionName

val Context.versionCode
  get() = versionCode(packageName)

inline fun Context.versionCode(packageName: String) =
  packageManager.getPackageInfo(packageName, 0)?.versionCode ?: 0

fun Context.metaDataString(key: String) =
  packageManager.getApplicationInfo(
    packageName,
    PackageManager.GET_META_DATA
  ).metaData?.getString(key)

fun Context.metaDataInt(key: String) =
  packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData?.getInt(
    key
  )

val Context.imei
  @SuppressLint("MissingPermission", "HardwareIds")
  get() = runCatching {
    Settings.System.getString(contentResolver, Secure.ANDROID_ID) ?: (getSystemService(
      Context.TELEPHONY_SERVICE
    ) as TelephonyManager).deviceId
  }.getOrNull()

/**
 * 应用是否存在.
 * @receiver Context
 * @param packageName String 应用包名.
 * @return Boolean
 */
inline fun Context.appExists(packageName: String) =
  packageManager.getLaunchIntentForPackage(packageName) != null

/**
 * 应用是否存在.
 * @receiver Context
 * @param packageName String 应用包名.
 * @return Boolean
 */
inline fun Context?.checkAppInstalled(packageName: String): Boolean {
  if (packageName.trim().isEmpty()) {
    return false
  }

  return try {
    val packageInfo = this?.packageManager?.getPackageInfo(packageName, 0)
    packageInfo != null
  } catch (e: Exception) {
    false
  }
}

/**
 * activity是否存在.
 * @receiver Context
 * @param packageName String 应用包名.
 * @param activityName String activity名.
 * @return Boolean
 */
inline fun Context.activityExists(packageName: String, activityName: String) =
  Intent().let {
    it.setClassName(packageName, activityName)
    packageManager.resolveActivity(
      it,
      0
    ) != null || it.resolveActivity(packageManager) != null || packageManager.queryIntentActivities(
      it,
      0
    ).size > 0
  }

/**
 * 启动app.
 * @receiver Context
 * @param pkgName String
 */
inline fun Context.launchApp(pkgName: String = packageName) {
  runCatching {
    packageManager.getLaunchIntentForPackage(pkgName)?.let {
      it.newTask()
      startActivity(it)
    }
  }
}

/**
 * 当前进程是否存在
 * @param pid
 * @return
 */
inline fun Context.isProcessExist(pid: Int): Boolean {
  val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
  val runningAppProcesses = activityManager.runningAppProcesses
  runningAppProcesses.forEach {
    if (it.pid == pid) {
      return true
    }
  }
  return false
}

/**
 * 判断通知权限是否开启.
 * @receiver Context
 * @return Boolean
 */
inline fun Context.notificationEnable() =
  NotificationManagerCompat.from(this).areNotificationsEnabled()

/**
 * 调用系统浏览器.
 * @receiver Context
 * @param url String
 * @param newTask Boolean 是否添加FLAG_ACTIVITY_NEW_TASK
 * @return Boolean
 */
inline fun Context.browse(url: String, newTask: Boolean = false): Boolean =
  kotlin.runCatching {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    if (newTask) {
      intent.newTask()
    }
    startActivity(intent)
    true
  }.getOrNull() ?: false

/**
 * 安装apk.
 * @receiver Context
 * @param apkFile File
 */
inline fun Context.install(apkFile: File) {
  val install = Intent(Intent.ACTION_VIEW).newTask()
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    val apkUri =
      FileProvider.getUriForFile(this, "$packageName.ExtensionsFileProvider", apkFile)
    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    install.setDataAndType(apkUri, "application/vnd.android.package-archive")
  } else {
    install.setDataAndType(
      Uri.parse("file://" + apkFile.absolutePath),
      "application/vnd.android.package-archive"
    )
  }
  startActivity(install)
}

inline fun Context.dp2px(dp: Float) =
  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

inline fun Context.dp2px(dp: Int) = dp2px(dp.toFloat()).toInt()

inline fun Context.sp2px(sp: Float) =
  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

inline fun Context.sp2px(sp: Int) = sp2px(sp.toFloat()).toInt()

inline fun Context.px2dp(px: Int) = px / resources.displayMetrics.density
inline fun Context.px2sp(px: Int) = px / resources.displayMetrics.scaledDensity

inline val Context.densityDpi
  get() = resources.displayMetrics.densityDpi
inline val Context.density
  get() = resources.displayMetrics.density

inline val Context.screenWidth
  get() = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width
inline val Context.screenHeight
  get() = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height

inline val Context.statusbarHeight
  get() = resources.getDimensionPixelSize(
    Resources.getSystem().getIdentifier(
      "status_bar_height",
      "dimen",
      "android"
    )
  )

inline val Context.actionbarSize: Int
  get() {
    val ta = obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val value = ta.getDimensionPixelSize(ta.getIndex(0), 0)
    ta.recycle()
    return value
  }

inline val Context.selectableItemBackgroundResId
  get() = TypedValue().apply {
    theme.resolveAttribute(
      R.attr.selectableItemBackground,
      this,
      true
    )
  }.resourceId

inline val Context.selectableItemBackgroundBorderlessResId
  get() = TypedValue().apply {
    theme.resolveAttribute(
      R.attr.selectableItemBackgroundBorderless,
      this,
      true
    )
  }.resourceId

/**
 * 加载布局文件.
 * @receiver Context
 * @param resId Int
 * @param parent ViewGroup?
 * @param attachToRoot Boolean
 * @return View
 */
inline fun Context.inflate(
  resId: Int,
  parent: ViewGroup? = null,
  attachToRoot: Boolean = false
): View =
  LayoutInflater.from(this).inflate(resId, parent, attachToRoot)

/**
 * 系统拨号.
 * @receiver Context
 * @param number String
 * @return Boolean
 */
@RequiresPermission(Manifest.permission.CALL_PHONE)
inline fun Context.makeCall(number: String): Boolean =
  kotlin.runCatching {
    startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")).newTask())
    true
  }.onFailure {
    it.printStackTrace()
  }.getOrNull() ?: false

/**
 * 跳转到拨号页面.
 * @receiver Context
 * @param number String
 * @return Boolean
 */
inline fun Context.makeDial(number: String): Boolean =
  kotlin.runCatching {
    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).newTask())
    true
  }.onFailure {
    it.printStackTrace()
  }.getOrNull() ?: false

/**
 * 分享.
 * @receiver Context
 * @param text String 内容.
 * @param subject String 主题.
 * @return Boolean
 */
inline fun Context.share(text: String, subject: String = ""): Boolean =
  kotlin.runCatching {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    startActivity(Intent.createChooser(intent, null))
    true
  }.onFailure {
    it.printStackTrace()
  }.getOrNull() ?: false

/**
 * 构建Intent。
 * @receiver Context
 * @param params Array<out Pair<String, Any?>>
 * @return Intent
 */
inline fun <reified T : Any> Context.intent(vararg params: Pair<String, Any?>): Intent =
  ComInternals.createIntent(this, T::class.java, params)

/**
 * 将字符串复制到粘贴板
 * @param content
 */
inline fun Context.setClipboardContent(content: String) {
  val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  clipboard.setPrimaryClip(ClipData.newPlainText(null, content))
}

/**
 * 获取粘贴板的字符串
 * @return String
 */
inline fun Context.getClipboardContent(): String {
  val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  val primaryClip = clipboard.primaryClip
  if (primaryClip != null && primaryClip.itemCount > 0) {
    return primaryClip.getItemAt(0).text.toString()
  }
  return ""
}

/**
 * 判断当前进程是否在住进程
 * @return
 */
@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
inline fun Context.isMainProcess(): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    Application.getProcessName() == packageName
  } else {
    try {
      Class.forName("android.app.ActivityThread")
        .getDeclaredMethod("currentProcessName")
        .invoke(null) == packageName
    } catch (e: Exception) {
      return ProcessUtil.isMainProcess(this)
    }
  }
}
