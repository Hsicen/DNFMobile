package com.hsicen.core.ui.dialog

import android.content.Context
import android.os.Bundle
import android.transition.TransitionManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hsicen.core.R
import com.hsicen.extensions.extensions.inflate
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.dialog_loading.view.*

/**
 * 作者：hsicen  2020/8/22 11:31
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：默认的Loading加载弹窗
 */
class KLoadingDialog private constructor(
  context: Context,
  private val builder: Builder
) : AlertDialog(context, R.style.Loading) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val root = context.inflate(R.layout.dialog_loading)

    // activity设置了content transition属性 这里会抛转transitionManager空指针异常 这里做法是new一个新的
    runCatching {
      if (window?.transitionManager == null) {
        window?.transitionManager = TransitionManager()
      }

      window?.setContentView(root)
    }.onFailure {
      Logger.e(it, "默认加载弹窗")
    }
    setContentView(R.layout.dialog_loading)

    setCancelable(builder.isCancelable)
    setCanceledOnTouchOutside(builder.isCanceledOnTouchOutside)

    Glide.with(root.ivLoading)
      .asGif()
      .load(R.drawable.gif_loading)
      .into(root.ivLoading)
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

fun Context.showLoading(builder: (KLoadingDialog.Builder.() -> Unit)? = null): KLoadingDialog =
  KLoadingDialog.show(this, builder)

fun Fragment.showLoading(builder: (KLoadingDialog.Builder.() -> Unit)? = null): KLoadingDialog? =
  KLoadingDialog.show(this, builder)
