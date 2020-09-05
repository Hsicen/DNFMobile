package com.hsicen.core.data

import android.content.Context
import androidx.annotation.CallSuper
import com.hsicen.core.coreComponent
import com.hsicen.core.utils.IRelease
import com.hsicen.extensions.utils.GlobalContext
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 作者：hsicen  2020/9/5 23:45
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：数据服务实现基类
 */
abstract class BaseDataServiceImpl<T> : IRelease {

  protected val context: Context = GlobalContext.getContext()

  protected var rootJob = Job()
    get() {
      if (field.isCancelled) {
        field = Job()
      }
      return field
    }

  /** 核心组件. */
  protected val coreComponent by lazy { context.coreComponent() }

  abstract val service: T

  /**
   * 安全启动.
   * @param block suspend CoroutineScope.() -> Unit
   * @return JobWrapper
   */
  fun safetyLaunch(block: suspend CoroutineScope.() -> Unit): JobWrapper {
    val jobWrapper = JobWrapper()
    jobWrapper.job = GlobalScope.launch(CoroutinesDispatchers.ui + rootJob) {
      runCatching {
        block.invoke(this)
      }.onFailure {
        jobWrapper.onFailure?.invoke(it) ?: Logger.e(it, "safetyLaunch")
      }
    }
    return jobWrapper
  }

  /**
   * 启动协程序.
   * @param block suspend CoroutineScope.() -> Unit
   * @return Job
   */
  fun launch(block: suspend CoroutineScope.() -> Unit): Job =
    GlobalScope.launch(CoroutinesDispatchers.ui + rootJob, block = block)

  @CallSuper
  override fun release() {
    rootJob.cancel()
  }

}
