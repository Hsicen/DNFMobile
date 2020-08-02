package com.hsicen.core.ui.loading

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hsicen.core.R
import com.hsicen.extensions.extensions.inflate
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.dialog_k_loading.view.*

/**
 * 作者：hsicen  2020/8/2 23:36
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Loading弹窗
 */
class KLoadingDialog private constructor(
    context: Context,
    private val builder: Builder
) : AlertDialog(context, R.style.Loading) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = context.inflate(R.layout.dialog_k_loading)
        // activity设置了content transition属性 这里会抛转transitionManager空指针异常 这里做法是new一个新的
        runCatching {
            if (window?.transitionManager == null) {
                window?.transitionManager = TransitionManager()
            }

            window?.setContentView(root)
        }.onFailure {
            Logger.e(it, "")
        }
        setContentView(R.layout.dialog_k_loading)

        setCancelable(builder.isCancelable)
        setCanceledOnTouchOutside(builder.isCanceledOnTouchOutside)

        // TODO: 2020/8/2 统一封装Glide 不要单独使用
        Glide.with(root.iv_k_loading)
            .asGif()
            .load(R.drawable.gif_loading)
            .into(root.iv_k_loading)
    }

    class Builder {
        var isCancelable = false
        var isCanceledOnTouchOutside = false
    }

    companion object {
        /**
         * 显示.
         * @param context Context
         * @param builder (Builder.() -> Unit)?
         * @return KLoadingDialog
         */
        fun show(context: Context, builder: (Builder.() -> Unit)? = null): KLoadingDialog =
            KLoadingDialog(context, Builder().apply { builder?.invoke(this) }).apply {
                show()
            }

        /**
         * 显示.
         * @param fragment Fragment
         * @param builder (Builder.() -> Unit)?
         * @return KLoadingDialog?
         */
        fun show(fragment: Fragment, builder: (Builder.() -> Unit)? = null): KLoadingDialog? =
            fragment.activity?.let { show(it, builder) }
    }
}

fun Context.showKLoading(builder: (KLoadingDialog.Builder.() -> Unit)? = null): KLoadingDialog =
    KLoadingDialog.show(this, builder)

fun Fragment.showKLoading(builder: (KLoadingDialog.Builder.() -> Unit)? = null): KLoadingDialog? =
    KLoadingDialog.show(this, builder)