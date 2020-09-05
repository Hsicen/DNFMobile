package com.hsicen.core.arch

import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import com.hsicen.core.coreComponent
import com.hsicen.core.exceptions.handleForNetwork
import com.hsicen.core.ui.loading.LoadingDialog
import com.hsicen.core.ui.loading.showLoading
import com.hsicen.extensions.extensions.hideKeyboard
import com.hsicen.extensions.extensions.hideKeyboardWithDelay
import com.hsicen.extensions.extensions.yes


/**
 * 核心Activity
 * Created by tomlezen.
 * Data: 2019/3/22.
 * Time: 11:42.
 * @property layoutResID: Int 界面布局ID.
 */
abstract class CoreActivity(
  @LayoutRes private val layoutResID: Int
) : AppCompatActivity() {

  var kLoading: LoadingDialog? = null
    set(value) {

      // 先判断并取消掉上一个loading
      if (field?.isShowing == true) {
        field?.dismiss()
      }
      field = value
    }

  /** 点击外部关闭软件盘. */
  var touchOutSideCloseKeyboard = false

  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layoutResID)

    initVariable()
    initView()
    initData()
  }

  /*** 初始化Intent数据*/
  protected open fun initVariable() {}

  /*** 初始化View*/
  protected open fun initView() {}

  /*** 初始化数据*/
  protected open fun initData() {}

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> {
      onBackPressed()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    val result = super.dispatchTouchEvent(ev)
    (!result && touchOutSideCloseKeyboard && ev?.action == MotionEvent.ACTION_DOWN).yes {
      val view = currentFocus
      (isHideKeyboard(view ?: return@yes, ev ?: return@yes)).yes {
        view.hideKeyboardWithDelay()
        view.clearFocus()
      }
    }
    return result
  }

  override fun onDestroy() {
    hideKeyboard()
    super.onDestroy()
    coreComponent().refWatcher()?.watch(this, "${this::class.simpleName} was detached")
  }

  /**
   * 判断是否需要隐藏软键盘.
   * @param v View
   * @param ev MotionEvent
   * @return Boolean
   */
  private fun isHideKeyboard(v: View, ev: MotionEvent): Boolean {
    if (v is EditText) {
      val location = IntArray(2)
      v.getLocationInWindow(location)
      val left = location[0]
      val top = location[1]
      val bottom = top + v.getHeight()
      val right = left + v.getWidth()
      return !(ev.x > left && ev.x < right && ev.y > top && ev.y < bottom)
    }
    return false
  }

  /**
   * Fragment导航.
   * @param hostId Int
   * @param id Int
   * @param bundle Bundle?
   * @param navigatorExtras Navigator.Extras?
   * @param optionsBuilder (NavOptions.Builder.() -> Unit)
   */
  fun nav(
    @IdRes hostId: Int,
    @IdRes id: Int,
    bundle: Bundle? = null,
    navigatorExtras: Navigator.Extras? = null,
    optionsBuilder: (NavOptions.Builder.() -> Unit) = {}
  ) {
    Navigation.findNavController(this, hostId)
      .navigate(
        id,
        bundle,
        defNavOptions.apply {
          optionsBuilder.invoke(this)
        }.build(),
        navigatorExtras
      )
  }

  /**
   * LiveData 扩展.
   * @receiver LiveData<T>
   * @param block (data: T) -> Unit
   */
  infix fun <T> LiveData<T>?.observe(block: (data: T) -> Unit) =
    this?.observe(this@CoreActivity, Observer { block.invoke(it) })

  /**
   * 加载状态默认处理.
   * @receiver LiveData<LoadState>?
   * @param hookLoading (() -> Boolean)?
   * @param hookLoaded ((LoadState.Loaded) -> Boolean)?
   * @param hookLoadError ((LoadState.LoadError) -> Boolean)?
   */
  fun LiveData<LoadState>?.observeDefHandle(
    hookLoading: (() -> Boolean)? = null,
    hookLoaded: ((LoadState.Loaded) -> Boolean)? = null,
    hookLoadError: ((LoadState.LoadError) -> Boolean)? = null
  ) {
    this observe {
      when (it) {
        is LoadState.Loading -> {
          if (hookLoading?.invoke() != true) {
            if (kLoading?.isShowing != true) {
              kLoading = showLoading()
            }
          }
        }

        is LoadState.Loaded -> {
          if (hookLoaded?.invoke(it) != true) {
            kLoading?.dismiss()
          }
        }

        is LoadState.LoadError -> {
          if (hookLoadError?.invoke(it) != true) {
            kLoading?.dismiss()
            it.exception?.handleForNetwork()
          }
        }
      }
    }
  }
}

/**
 * 绑定toolbar以及返回按钮
 * @receiver AppCompatActivity
 * @param toolbar Toolbar?
 */
fun AppCompatActivity.setToolbarWithBack(toolbar: Toolbar?) {
  setSupportActionBar(toolbar)
  supportActionBar?.setDisplayHomeAsUpEnabled(true)
  toolbar?.setNavigationOnClickListener {
    onBackPressed()
  }
}