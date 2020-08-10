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
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
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
    }

    /**
     * 调用懒加载.
     * 只会调用一次.
     */
    private fun invokeLazyInit() {
        if (isLazyInit) return
        onLazyInit()
        isLazyInit = true
    }

    /**
     * 返回.
     */
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
     * LiveData 扩展.
     * @receiver LiveData<T>
     * @param block (data: T) -> Unit
     */
    infix fun <T> LiveData<T>?.observe(block: (data: T) -> Unit) =
        this?.observe(this@CoreFragment, Observer { block.invoke(it) })

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
