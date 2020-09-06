package com.hsicen.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RadioButton
import android.widget.RadioGroup

class KTabGroup @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : RadioGroup(context, attrs) {

  private var repeatClickEnable = true

  private var mOnTabCheckedChangeListener: OnTabCheckedChangeListener? = null

  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
    when (ev.action) {
      MotionEvent.ACTION_UP -> {
        val childCount = childCount
        for (index in 0 until childCount) {
          val child = getChildAt(index)
          val transformX = scrollX + ev.x
          val transformY = scrollY + ev.y
          if (child.left <= transformX && child.top <= transformY && child.right >= transformX && child.bottom >= transformY) {
            if (!repeatClickEnable && child is RadioButton && child.isChecked) {
              return true
            }
            if (null != mOnTabCheckedChangeListener && mOnTabCheckedChangeListener!!.onCheckedPreChange(this, child.id)) {
              return true
            }
          }
        }
      }
    }
    return super.onInterceptTouchEvent(ev)
  }

  fun setOnTabCheckedChangeListener(onTabCheckedChangeListener: OnTabCheckedChangeListener) {
    mOnTabCheckedChangeListener = onTabCheckedChangeListener
    super.setOnCheckedChangeListener(onTabCheckedChangeListener)
  }

  fun setRepeatClickEnable(enable: Boolean) {
    repeatClickEnable = enable
  }

  interface OnTabCheckedChangeListener : OnCheckedChangeListener {
    fun onCheckedPreChange(tabGroup: KTabGroup, checkedId: Int): Boolean
  }
}
