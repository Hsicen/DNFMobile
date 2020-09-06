package com.hsicen.core.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.hsicen.core.R
import com.hsicen.core.arch.CoreDialogFragment
import com.hsicen.extensions.extensions.clickThrottle
import kotlinx.android.synthetic.main.dialog_ios.*

/**
 * 作者：hsicen  2020/9/6 16:26
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：ios风格的dialog
 */
abstract class KIosDialog : CoreDialogFragment() {

  override val layoutId: Int
    get() = R.layout.dialog_ios

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = super.onCreateView(inflater, container, savedInstanceState)
    layoutInflater.inflate(getContentLayoutId(), view?.findViewById<FrameLayout>(R.id.fl_dialog_content))
    return view
  }

  @CallSuper
  override fun onInit(savedInstanceState: Bundle?) {
    val negativeText = getNegativeText()
    tv_negative.text = negativeText
    tv_negative.clickThrottle(block = ::onNegativeBtnClick)
    val positiveText = getPositiveText()
    tv_positive.text = positiveText
    tv_positive.clickThrottle(block = ::onPositiveBtnClick)

    if (positiveText.isEmpty()) {
      view_button_divider.isVisible = false
      tv_positive.isVisible = false
    }
    if (negativeText.isEmpty()) {
      view_button_divider.isVisible = false
      tv_negative.isVisible = false
    }
  }

  /**
   * 更新右按钮文字.
   * @param text String
   */
  fun updatePositiveText(text: String) {
    tv_positive?.text = text
  }

  open fun onNegativeBtnClick(view: View) {
    dismiss()
  }

  open fun onPositiveBtnClick(view: View) {
    dismiss()
  }

  /**
   * 内容布局id.
   * @return Int
   */
  protected abstract fun getContentLayoutId(): Int

  abstract fun getNegativeText(): CharSequence
  abstract fun getPositiveText(): CharSequence
}