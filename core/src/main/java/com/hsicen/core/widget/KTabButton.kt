package com.hsicen.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.hsicen.core.R

class KTabButton @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatRadioButton(context, attrs, defStyleAttr) {

  private val redDot: Drawable? = context.getDrawable(R.drawable.shape_red_dot)
  private var showRedDot: Boolean = false

  init {
    redDot?.setBounds(0, 0, redDot.intrinsicWidth, redDot.intrinsicHeight)
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    if (showRedDot && null != redDot && null != compoundDrawables[1]) {
      val compoundPaddingLeft = compoundPaddingLeft
      val compoundPaddingRight = compoundPaddingRight
      val scrollX = scrollX
      val scrollY = scrollY
      val right = right
      val left = left
      val hspace = right - left - compoundPaddingRight - compoundPaddingLeft
      val redDotWidth = redDot.intrinsicWidth
      val redDotHeight = redDot.intrinsicHeight
      canvas?.save()
      canvas?.translate(
        (scrollX + compoundPaddingLeft
            + (hspace - compoundDrawables[1].intrinsicWidth) / 2 + compoundDrawables[1].intrinsicWidth - (redDotWidth / 2)).toFloat(),
        (scrollY + paddingTop - redDotHeight / 2).toFloat()
      )
      redDot.draw(canvas!!)
      canvas.restore()
    }
  }

  fun setRedDot(show: Boolean) {
    showRedDot = show
    invalidate()
  }
}

