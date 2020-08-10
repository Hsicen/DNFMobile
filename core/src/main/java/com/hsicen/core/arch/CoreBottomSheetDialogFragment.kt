package com.hsicen.core.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hsicen.extensions.extensions.forbidScroll
import com.hsicen.extensions.extensions.hideKeyboard
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.extensions.yes

/**
 * 作者：hsicen  2020/8/10 21:55
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：底部弹窗
 */
abstract class CoreBottomSheetDialogFragment : BottomSheetDialogFragment() {

    /** 布局文件id. */
    @get: LayoutRes
    protected abstract val layoutId: Int

    /** View是否已经创建. */
    protected var isViewCreated = false

    /** 根布局. */
    protected var root: View? = null

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            val dialogState = savedInstanceState.getBundle("android:savedDialogState")
            if (dialogState != null) {
                dialog?.onRestoreInstanceState(dialogState)
            }
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated.no {
            onInit(savedInstanceState)
        }
        isViewCreated = true
        forbidScroll()
    }

    @CallSuper
    override fun onDestroyView() {
        isViewCreated = false
        dialog?.setOnCancelListener(null)
        dialog?.setOnDismissListener(null)
        super.onDestroyView()
        hideKeyboard()
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
        this.observe(this@CoreBottomSheetDialogFragment, Observer { block.invoke(it) })
}