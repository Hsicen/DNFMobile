package com.hsicen.core.arch

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsicen.core.data.CoroutinesDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


/**
 * 作者：hsicen  2020/8/10 22:09
 * 邮箱：codinghuang@163.com
 * 功能：封装协程作用域.
 * 描述：核心ViewModel
 */
abstract class CoreViewModel : ViewModel(), CoroutineScope {

  private val rootJob = Job()

  override val coroutineContext: CoroutineContext
    get() = CoroutinesDispatchers.ui + rootJob

  /** 加载状态. */
  protected val _loadingState = MutableLiveData<LoadState>()
  val loadState: LiveData<LoadState>
    get() = _loadingState

  @CallSuper
  override fun onCleared() {
    rootJob.cancel()
    super.onCleared()
  }
}

/**
 * 加载状态.
 */
sealed class LoadState {

  /**
   * 加载中.
   */
  object Loading : LoadState()

  /**
   * 加载完成.
   * @property data Any 数据.
   * @constructor
   */
  data class Loaded(private val data: Any = Any()) : LoadState() {
    fun <T> data() = data as T
  }

  /**
   *  加载失败.
   * @property exception Exception?
   * @constructor
   */
  data class LoadError(val exception: Throwable? = null) : LoadState()
}
