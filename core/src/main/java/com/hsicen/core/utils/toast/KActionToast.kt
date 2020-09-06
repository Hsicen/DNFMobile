package com.hsicen.core.utils.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import com.hsicen.core.R


class KActionToast(context: Context) : View.OnClickListener {
  private val view: View
  private val close: ImageButton
  private val content: TextView
  private val action: Button
  private val window: PopupWindow

  private var actionCallback: (() -> Unit)? = null
  private var locationTemp = IntArray(2)

  init {
    view = LayoutInflater.from(context).inflate(R.layout.action_toast, null)
    close = view.findViewById(R.id.close)
    content = view.findViewById(R.id.content)
    action = view.findViewById(R.id.action)
    action.setOnClickListener(this)
    close.setOnClickListener(this)

    window = PopupWindow(
      view,
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    window.animationStyle = R.style.Animation_KActionToast
  }

  private val dismissRunnable = Runnable {
    window.dismiss()
  }

  fun show(
    view: View,
    offsetY: Int = 0,
    content: Int,
    action: Int,
    actionCallback: (() -> Unit)? = null
  ) {
    show(
      view,
      offsetY,
      view.context.getString(content),
      view.context.getString(action),
      actionCallback
    )
  }

  /**
   * 覆盖在指定View的底部
   */
  fun show(
    anchor: View,
    offsetY: Int = 0,
    content: CharSequence,
    action: CharSequence,
    actionCallback: (() -> Unit)? = null
  ) {
    this.content.text = content
    this.action.text = action
    this.actionCallback = actionCallback
    view.measure(View.MeasureSpec.makeMeasureSpec(anchor.resources.displayMetrics.widthPixels, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(anchor.resources.displayMetrics.heightPixels, View.MeasureSpec.AT_MOST))

    anchor.getLocationInWindow(locationTemp)
    window.showAtLocation(anchor, Gravity.TOP or Gravity.START, 0, locationTemp[1] - view.measuredHeight + anchor.height + offsetY)
    anchor.removeCallbacks(dismissRunnable)
    anchor.postDelayed(dismissRunnable, 3000)
  }

  override fun onClick(v: View?) {
    when (v) {
      close -> window.dismiss()
      action -> {
        actionCallback?.invoke()
      }
    }
  }

  companion object {
    private var sharedInstance: KActionToast? = null

    fun getInstance(context: Context): KActionToast {
      if (null == sharedInstance) {
        sharedInstance = KActionToast(context)
      }
      return sharedInstance!!
    }
  }
}
