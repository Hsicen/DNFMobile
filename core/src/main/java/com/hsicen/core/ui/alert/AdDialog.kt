package com.hsicen.core.ui.alert

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.hsicen.core.R
import com.hsicen.core.coreComponent
import com.hsicen.extensions.extensions.click
import com.hsicen.extensions.extensions.clickThrottle
import com.hsicen.extensions.extensions.inflate
import kotlinx.android.synthetic.main.dialog_ad.view.*

/**
 * 作者：hsicen  2020/9/6 0:02
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：广告弹窗
 */
class AdDialog(
  context: Context,
  private val builder: Builder
) : AlertDialog(context, R.style.Dialog_Default) {

  private var negativeClick: ((AdDialog) -> Unit)? = null
  private var positiveClick: ((AdDialog) -> Unit)? = null
  private var imgClick: ((AdDialog) -> Unit)? = null
  private var onDismiss: (() -> Unit)? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val root = context.inflate(R.layout.dialog_ad)
    window?.setContentView(root)

    window?.setBackgroundDrawableResource(android.R.color.transparent)

    setCancelable(builder.isCancelable)
    setCanceledOnTouchOutside(builder.isCanceledOnTouchOutside)

    // 初始化界面

    root.tv_kdialog_title.text = builder.title
    root.img_kdialog_message.loadImage(builder.imgUrl)

    // 如果是紧急弹窗
    if (builder.isEmergency) {
      root.tv_kdialog_close.click {
        context.coreComponent().activityManager().exitApp()
      }
      root.tv_kdialog_title.isVisible = false
      root.img_close.isVisible = false
      root.tv_kdialog_close.isVisible = true

      root.img_kdialog_message.clickThrottle {
        imgClick?.invoke(this@AdDialog)
      }
    } else {
      /***  广告跳转.*/
      root.img_kdialog_message.clickThrottle {
        imgClick?.invoke(this@AdDialog)
      }

      root.tv_kdialog_title.isVisible = false
      root.img_close.isVisible = true
      root.tv_kdialog_close.isVisible = false
      root.img_close.click {
        dismiss()
      }
    }

    setOnDismissListener {
      onDismiss?.invoke()
    }
  }

  /**
   * 取消按钮点击.
   * @param negativeClick ((AdDialog) -> Unit)
   * @return AdDialog
   */
  fun onNegativeButtonClick(negativeClick: ((AdDialog) -> Unit)): AdDialog {
    this.negativeClick = negativeClick
    return this
  }

  fun onImgClick(imgClick: ((AdDialog) -> Unit)): AdDialog {
    this.imgClick = imgClick
    return this
  }

  /**
   * 确认按钮点击.
   * @param positiveClick ((AdDialog) -> Unit)
   * @return AdDialog
   */
  fun onPositiveButtonClick(positiveClick: ((AdDialog) -> Unit)): AdDialog {
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
   * @return AdDialog
   */
  internal fun showInternal(): AdDialog {
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
    var imgUrl: String = ""
    var isCancelable = true
    var isCanceledOnTouchOutside = true
    var isEmergency = false
    var jumpTo: String = ""
  }

  companion object {

    /**
     * 显示弹窗.
     * @param context FragmentManager
     * @param builder Builder.() -> Unit
     * @return AdDialog
     */
    fun show(context: Context, builder: Builder.() -> Unit): AdDialog =
      AdDialog(context, Builder().apply(builder)).showInternal()

    /**
     * 显示弹窗.
     * @param fragment Fragment
     * @param builder Builder.() -> Unit
     * @return AdDialog?
     */
    fun show(fragment: Fragment, builder: Builder.() -> Unit): AdDialog? =
      fragment.activity?.let { show(it, builder) }
  }
}
