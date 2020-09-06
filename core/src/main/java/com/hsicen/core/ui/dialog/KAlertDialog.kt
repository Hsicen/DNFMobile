package com.hsicen.core.ui.dialog

import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

import com.hsicen.core.R
import com.hsicen.extensions.extensions.clickThrottle
import com.hsicen.extensions.extensions.inflate
import com.hsicen.extensions.extensions.yes
import com.hsicen.extensions.utils.setClickableMovementMethod
import kotlinx.android.synthetic.main.dialog_alert.view.*

/**
 * 作者：hsicen  2020/9/6 16:13
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Alert提示框
 */
class KAlertDialog(
  context: Context,
  private val builder: Builder
) : AlertDialog(context, R.style.Dialog_Default) {

  private var negativeClick: ((KAlertDialog) -> Unit)? = null
  private var positiveClick: ((KAlertDialog) -> Unit)? = null
  private var onDismiss: (() -> Unit)? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val root = context.inflate(R.layout.dialog_alert)
    window?.setContentView(root)

    setCancelable(builder.isCancelable)
    setCanceledOnTouchOutside(builder.isCanceledOnTouchOutside)

    // 初始化界面
    root.tv_kdialog_title.text = builder.title
    root.tv_kdialog_title.isVisible = builder.title.isNotBlank()
    root.tv_kdialog_message.text = builder.message
    root.tv_kdialog_message.movementMethod = LinkMovementMethod.getInstance()
    root.tv_kdialog_message.setClickableMovementMethod()
    builder.negativeText.isNotEmpty().yes {
      root.btn_kdialog_negative.text = builder.negativeText
      root.btn_kdialog_negative.isVisible = true
      root.btn_kdialog_negative.clickThrottle {
        negativeClick?.invoke(this@KAlertDialog) ?: dismiss()
      }
    }
    root.btn_kdialog_positive.text = builder.positiveText
    root.btn_kdialog_positive.isVisible = true
    root.btn_kdialog_positive.clickThrottle {
      positiveClick?.invoke(this@KAlertDialog) ?: dismiss()
    }

    setOnDismissListener {
      onDismiss?.invoke()

      onDismiss = null
      positiveClick = null
      negativeClick = null
      setOnDismissListener(null)
    }
  }

  /**
   * 取消按钮点击.
   * @param negativeClick ((KAlertDialog) -> Unit)
   * @return KAlertDialog
   */
  fun onNegativeButtonClick(negativeClick: ((KAlertDialog) -> Unit)): KAlertDialog {
    this.negativeClick = negativeClick
    return this
  }

  /**
   * 确认按钮点击.
   * @param positiveClick ((KAlertDialog) -> Unit)
   * @return KAlertDialog
   */
  fun onPositiveButtonClick(positiveClick: ((KAlertDialog) -> Unit)): KAlertDialog {
    this.positiveClick = positiveClick
    return this
  }

  /**
   * 弹窗消失.
   * @param onDismiss () -> Unit
   */
  fun onDismiss(onDismiss: () -> Unit) {
    this.onDismiss = onDismiss
  }

  /**
   * 显示.
   * @return KAlertDialog
   */
  internal fun showInternal(): KAlertDialog {
    show()
    return this
  }

  /**
   * Dialog参数构造器.
   * @property title CharSequence
   * @property message CharSequence
   * @property negativeText CharSequence
   * @property positiveText CharSequence
   * @property isCancelable Boolean
   * @property isCanceledOnTouchOutside Boolean
   */
  class Builder {
    var title: CharSequence = ""
    var message: CharSequence = ""
    var negativeText: CharSequence = ""
    var positiveText: CharSequence = "确定"
    var isCancelable = true
    var isCanceledOnTouchOutside = true
  }

  companion object {

    /**
     * 显示弹窗.
     * @param context FragmentManager
     * @param builder Builder.() -> Unit
     * @return KAlertDialog
     */
    fun show(context: Context, builder: Builder.() -> Unit): KAlertDialog =
      KAlertDialog(context, Builder().apply(builder)).showInternal()

    /**
     * 显示弹窗.
     * @param fragment Fragment
     * @param builder Builder.() -> Unit
     * @return KAlertDialog?
     */
    fun show(fragment: Fragment, builder: Builder.() -> Unit): KAlertDialog? =
      fragment.activity?.let { show(it, builder) }
  }
}

fun Context.showKAlert(builder: KAlertDialog.Builder.() -> Unit): KAlertDialog =
  KAlertDialog.show(this, builder)

fun Fragment.showKAlert(builder: KAlertDialog.Builder.() -> Unit): KAlertDialog? =
  KAlertDialog.show(this, builder)
