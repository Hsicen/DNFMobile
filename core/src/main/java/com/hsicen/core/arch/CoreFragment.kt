package com.hsicen.core.arch

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import com.hsicen.core.R
import com.hsicen.core.coreComponent
import com.hsicen.core.exceptions.handleForNetwork
import com.hsicen.core.ui.dialog.KLoadingDialog
import com.hsicen.core.ui.dialog.showLoading
import com.hsicen.extensions.extensions.hideKeyboard
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.extensions.yes

/**
 * 作者：hsicen  2020/8/10 21:13
 * 邮箱：codinghuang@163.com
 * 功能：实现 1. Fragment懒加载. 2. 防止界面重复创建.
 * 描述：核心Fragment
 */
abstract class CoreFragment : Fragment() {

  /** 布局文件id. */
  @get: LayoutRes
  protected abstract val layoutId: Int

  /** View是否已经创建. */
  protected var isViewCreated = false

  /** 是否调用懒加载. */
  protected var isLazyInit = false

  /** 根布局. */
  protected var root: View? = null

  var kKLoading: KLoadingDialog? = null
    set(value) {
      // 先判断并取消掉上一个loading
      if (field?.isShowing == true) {
        field?.dismiss()
      }
      field = value
    }

  private val onBackPressedCallback = object : OnBackPressedCallback(false) {
    override fun handleOnBackPressed() {
      onBackPress()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
  }

  final override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
    val animation = runCatching { AnimationUtils.loadAnimation(activity, nextAnim) }.getOrNull()
    return if (animation != null) {
      KAnimationSet(true).add(animation)
        .endWithAction {
          if (!isDetached) {
            onAnimationEnd(enter)
          }
        }
        .startWithAction {
          if (!isDetached) {
            onAnimationStart(enter)
          }
        }
    } else {
      onAnimationEnd(enter, true)
      null
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    (root != null).yes {
      (root?.parent as? ViewGroup)?.removeView(root)
    }.no {
      root = inflater.inflate(layoutId, container, false)
    }
    return root
  }

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    isViewCreated.no {
      onInit(savedInstanceState)
    }
    isViewCreated = true
  }

  @CallSuper
  override fun setUserVisibleHint(isVisibleToUser: Boolean) {
    super.setUserVisibleHint(isVisibleToUser)
    (isVisibleToUser && isViewCreated).yes {
      invokeLazyInit()
      onVisibleToUser()
    }
  }

  @CallSuper
  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    (!hidden && isViewCreated).yes {
      onVisibleToUser()
    }
  }

  @CallSuper
  override fun onResume() {
    super.onResume()
    (((!isLazyInit && userVisibleHint) || (isLazyInit && isRealVisible)) && isViewCreated).yes {
      invokeLazyInit()
      onVisibleToUser()
    }
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    hideKeyboard()
  }

  override fun onDestroy() {
    isLazyInit = false
    isViewCreated = false
    super.onDestroy()
    activity?.coreComponent()?.refWatcher()?.watch(this, "${this::class.simpleName} was detached")
  }

  /*** 调用懒加载. 只会调用一次.*/
  private fun invokeLazyInit() {
    if (isLazyInit) return
    onLazyInit()
    isLazyInit = true
  }

  /*** 返回处理*/
  fun back() {
    NavHostFragment.findNavController(this).navigateUp().no { activity?.finish() }
  }

  /**
   * Fragment转场动画开始触发.
   * @param enter Boolean
   */
  @CallSuper
  protected open fun onAnimationStart(enter: Boolean) {
    if (enter) {
      root?.alpha = 0f
      root?.animate()?.alpha(1f)?.setDuration(200)?.start()
    }
  }

  /**
   * Fragment转场动画结束触发.
   * @param enter Boolean
   * @param isNoAnim Boolean 无动画.
   */
  @CallSuper
  protected open fun onAnimationEnd(enter: Boolean, isNoAnim: Boolean = false) {
    if (!enter && !isNoAnim) {
      root?.alpha = 0f
    }
  }

  /**
   * 当Fragment显示 和 执行 onResume方法时调用.
   * 该方法可用于页面刷新触发入口.
   */
  protected open fun onVisibleToUser() {
  }

  protected fun setBackPressEnable(enable: Boolean) {
    onBackPressedCallback.isEnabled = enable
  }

  protected open fun onBackPress() {}

  /**
   * 在onViewCreated执行时调用.
   * 这里一般用作view的初始化操作，如按钮点击事件等.
   * @param savedInstanceState Bundle?
   */
  abstract fun onInit(savedInstanceState: Bundle?)

  /**
   * 在Fragment展示在前台时调用.
   * 这里一般用作数据相关的初始化操作，如加载数据等.
   */
  abstract fun onLazyInit()

  /**
   * Navigation导航.
   * @param parentId Int
   * @param id Int
   * @param bundle Bundle?
   * @param navigatorExtras Navigator.Extras?
   * @param optionsBuilder (NavOptions.Builder.() -> Unit)
   */
  fun navToParent(
    @IdRes parentId: Int,
    @IdRes id: Int,
    bundle: Bundle? = null,
    navigatorExtras: Navigator.Extras? = null,
    optionsBuilder: (NavOptions.Builder.() -> Unit) = {}
  ) {
    if (activity is FragmentActivity) {
      Navigation.findNavController(requireActivity(), parentId)
        .navigate(
          id,
          bundle,
          defNavOptions.apply {
            optionsBuilder.invoke(this)
          }.build(),
          navigatorExtras
        )
    }
  }

  /**
   * LiveData 扩展.
   * @receiver LiveData<T>
   * @param block (data: T) -> Unit
   */
  infix fun <T> LiveData<T>?.observe(block: (data: T) -> Unit) =
    this?.observe(this@CoreFragment, Observer { block.invoke(it) })

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
            hideKeyboard()
            if (kKLoading?.isShowing != true) {
              kKLoading = showLoading()
            }
          }
        }
        is LoadState.Loaded -> {
          if (hookLoaded?.invoke(it) != true) {
            kKLoading?.dismiss()
          }
        }
        is LoadState.LoadError -> {
          if (hookLoadError?.invoke(it) != true) {
            kKLoading?.dismiss()
            it.exception?.handleForNetwork()
          }
        }
      }
    }
  }

  /** 是否真实为显示状态，需要判断父Fragment的visible状态. */
  private val isRealVisible: Boolean
    get() {
      var visible = this.isVisible
      var parentFragment: Fragment? = this
      while (parentFragment != null && visible) {
        visible = visible && parentFragment.isVisible
        parentFragment = parentFragment.parentFragment
      }
      return visible
    }
}

class KAnimationSet : AnimationSet {

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  constructor(shareInterpolator: Boolean) : super(shareInterpolator)

  private val animationListeners = mutableListOf<AnimationListener?>()

  init {
    super.setAnimationListener(object : AnimationListener {
      override fun onAnimationRepeat(p0: Animation?) {
        animationListeners.forEach { it?.onAnimationRepeat(p0) }
      }

      override fun onAnimationEnd(p0: Animation?) {
        animationListeners.forEach { it?.onAnimationEnd(p0) }
      }

      override fun onAnimationStart(p0: Animation?) {
        animationListeners.forEach { it?.onAnimationStart(p0) }
      }
    })
  }

  override fun setAnimationListener(listener: AnimationListener?) {
    if (!animationListeners.contains(listener)) {
      animationListeners.add(listener)
    }
  }

  fun add(animation: Animation?): KAnimationSet {
    addAnimation(animation)
    return this
  }
}

inline fun KAnimationSet.endWithAction(crossinline action: () -> Unit): KAnimationSet {
  setAnimationListener(object : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
      action()
    }

    override fun onAnimationStart(p0: Animation?) {
    }
  })
  return this
}

inline fun KAnimationSet.startWithAction(crossinline action: () -> Unit): KAnimationSet {
  setAnimationListener(object : Animation.AnimationListener {
    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
    }

    override fun onAnimationStart(p0: Animation?) {
      action()
    }
  })
  return this
}

val defNavOptions: NavOptions.Builder
  get() = NavOptions.Builder()
    .setEnterAnim(R.anim.activity_open_enter_from_right)
    .setExitAnim(R.anim.activity_open_exit_from_left)
    .setPopEnterAnim(R.anim.activity_close_enter_from_left)
    .setPopExitAnim(R.anim.activity_close_exit_from_right)