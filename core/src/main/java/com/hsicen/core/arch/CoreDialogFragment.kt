package com.hsicen.core.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.hsicen.core.R
import com.hsicen.extensions.extensions.hideKeyboard
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.extensions.yes


/**
 * 作者：hsicen  2020/8/10 21:50
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：核心DialogFragment
 */
abstract class CoreDialogFragment : DialogFragment() {

    /** 布局文件id. */
    @get: LayoutRes
    protected abstract val layoutId: Int

    /** 根布局. */
    protected var root: View? = null

    /** View是否已经创建. */
    protected var isViewCreated = false

    override fun getTheme(): Int = R.style.Dialog_Default_Transparent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        (root != null).yes {
            (root?.parent as? ViewGroup)?.removeView(root)
        }.no {
            root = inflater.inflate(layoutId, container, false)
        }
        return root
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            val dialogState = savedInstanceState.getBundle("android:savedDialogState")
            if (dialogState != null) {
                dialog?.onRestoreInstanceState(dialogState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated.no {
            onInit(savedInstanceState)
        }
        isViewCreated = true
    }

    @CallSuper
    override fun onDestroyView() {
        hideKeyboard()
        dialog?.setOnCancelListener(null)
        dialog?.setOnDismissListener(null)
        super.onDestroyView()
    }

    /**
     * 初始化.
     */
    abstract fun onInit(savedInstanceState: Bundle?)

    /**
     * LiveData 扩展.
     * @receiver LiveData<T>
     * @param block (data: T) -> Unit
     */
    infix fun <T> LiveData<T>.observe(block: (data: T) -> Unit) =
        this.observe(this@CoreDialogFragment, Observer { block.invoke(it) })
}