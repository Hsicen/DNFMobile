package com.hsicen.core.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 作者：hsicen  2020/8/30 16:45
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：全局Activity管理
 */
class ActivityManager(private val ctx: Context) {

  /**
   *  统计所有打开的app,用于退出应用
   */
  private val activityStack = Collections.synchronizedList(arrayListOf<Activity?>())

  private val resumedActivityStack = Collections.synchronizedSet(mutableSetOf<Activity?>())

  /** 当前activity . */
  var currentActivity: WeakReference<Activity>? = null

  /** app前台变化. */
  private val _appForegroundChange = MutableLiveData(false)
  val appForegroundChange: LiveData<Boolean>
    get() = _appForegroundChange

  /** 等待执行的任务队列（当进入到主界面时才执行任务）. */
  private val waitTaskQueue = ConcurrentLinkedQueue<(Activity) -> Unit>()

  private val activityLifecycleCallbacks by lazy {
    object : Application.ActivityLifecycleCallbacks {
      override fun onActivityPaused(activity: Activity) {
      }

      override fun onActivityResumed(activity: Activity) {
        activity.removeOrAddToList(false)
        Logger.v("onActivityResumed: $activity")
        // 每次执行resumed都触发一次等待任务检测
        triggerWaitTask(activity)
        currentActivity = WeakReference(activity)
      }

      override fun onActivityStarted(activity: Activity) {
      }

      override fun onActivityDestroyed(activity: Activity) {
        activityStack -= activity
        activity.removeOrAddToList(true)
        Logger.v("onActivityDestroyed: $activity")
      }

      override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
      }

      override fun onActivityStopped(activity: Activity) {
        activity.removeOrAddToList(true)
        Logger.i("onActivityStopped: $activity")
      }

      override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack += activity
      }
    }
  }

  /**
   * 从集合中移除或添加Activity.
   * @receiver Activity?
   * @param remove Boolean
   */
  fun Activity?.removeOrAddToList(remove: Boolean = false) {
    if (remove) {
      resumedActivityStack -= this
    } else {
      resumedActivityStack += this
    }

    val size = resumedActivityStack.size
    if (size == 0 && appForegroundChange.value != false) {
      _appForegroundChange.value = false
    } else if (size != 0 && appForegroundChange.value != true) {
      _appForegroundChange.value = true
    }
  }

  /**
   * 注册.
   * @param application Application
   */
  fun register(application: Application) {
    application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
    application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
  }

  /**
   * 退出app.
   */
  fun exitApp() {
    activityStack.forEach { it?.finish() }
    activityStack.clear()
    resumedActivityStack.clear()
    currentActivity = null
    waitTaskQueue.clear()
    android.os.Process.killProcess(android.os.Process.myPid())
  }

  /**
   * 获取activity栈.
   * @return List<Activity>
   */
  fun getActivityStack(): List<Activity?> = activityStack.toList()

  /**
   * 栈内是否包含主界面
   */
  fun containMainActivity(): Boolean {
    val find = activityStack.toList().find {
      it?.javaClass?.getAnnotation(MainActivity::class.java) != null
    }

    return find != null
  }

  /**
   * 当前界面是主界面
   */
  fun currentActivityIsMain(): Boolean {
    return currentActivity?.get()?.javaClass?.getAnnotation(MainActivity::class.java) != null
  }

  /**
   * 栈内是否包含Web界面
   */
  fun containWebActivity(): MutableList<Activity?> {
    val list = arrayListOf<Activity?>()
    activityStack.toList().forEach {
      val contain = it?.javaClass?.getAnnotation(WebActivity::class.java) != null
      if (contain) {
        list.add(it)
      }
    }
    return list
  }

  /**
   * 触发等待任务.
   * @param activity Activity
   */
  private fun triggerWaitTask(activity: Activity) {
    // 这里捕捉下异常 防止任务执行错误导致崩溃
    runCatching {
      // 检查栈里面是否有MainActivity
      if (waitTaskQueue.isNotEmpty() && containMainActivity()) {
        waitTaskQueue.poll()?.invoke(activity)
      }
    }
  }

  /**
   * 提交等待任务.
   * @param block Function1<Activity, Unit>
   */
  fun offerWaitTask(block: (Activity) -> Unit) {
    waitTaskQueue.offer(block)
  }

  /**
   * MainActivity标记.
   */
  @Target(AnnotationTarget.CLASS)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class MainActivity

  /**
   * KWebActivity标记.
   */
  @Target(AnnotationTarget.CLASS)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class WebActivity
}
