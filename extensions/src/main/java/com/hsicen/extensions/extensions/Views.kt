@file:Suppress("NOTHING_TO_INLINE", "WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET_ON_TYPE")

package com.hsicen.extensions.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger

/**
 * View控件扩展.
 * Created by tomlezen.
 * Data: 2019/4/13.
 * Time: 16:21.
 */

/**
 * 点击事件.
 * @receiver View
 * @param block (View) -> Unit
 */
inline fun View.click(noinline block: (View) -> Unit) {
  this.setOnClickListener(block)
}

/**
 * 点击事件防重复.
 * @receiver View
 * @param duration Long
 * @param block (View) -> Unit
 */
inline fun View.clickThrottle(duration: Long = 1000L, crossinline block: (View) -> Unit) {
  // 最新的点击时间.
  var lastClickTime = 0L
  this.setOnClickListener {
    if (System.currentTimeMillis() - lastClickTime > duration) {
      lastClickTime = System.currentTimeMillis()
      block.invoke(it)
    }
  }
}

/**
 * 点击事件防重复.
 * @receiver Array<View>
 * @param duration Long
 * @param block (View) -> Unit
 */
inline fun Array<View>.clickThrottle(duration: Long = 1000L, crossinline block: (View) -> Unit) {
  // 最新的点击时间.
  var lastClickTime = 0L
  this.forEach { v ->
    v.click {
      if (System.currentTimeMillis() - lastClickTime > duration) {
        lastClickTime = System.currentTimeMillis()
        block.invoke(it)
      }
    }
  }
}

operator fun View.plus(view: View): Array<View> = arrayOf(this, view)

/**
 *  @param isInflater 是否是inflater出来的.
 */
fun View.toBitmap(isInflater: Boolean = false): Bitmap? {
  return kotlin.runCatching {

    val width = if (this.width <= 0) screenWidth() else this.width
    val height = if (this.height <= 0) screenHeight() else this.height

    if (isInflater) {
      val measeurWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
      val measeurHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
      this.measure(measeurWidth, measeurHeight)
      this.layout(0, 0, this.measuredWidth, this.measuredHeight)
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    this.draw(canvas)
    bitmap
  }.onFailure {
    Logger.e("$it")
  }
    .getOrNull()

}

/** Y轴偏移值. */
val Paint.textOffsetY: Float
  get() = Math.abs(fontMetrics.top + fontMetrics.bottom) / 2

inline fun TextView.clearText() {
  text = ""
}

/**
 * 禁止滑动关闭.
 * @receiver BottomSheetDialogFragment
 */
inline fun BottomSheetDialogFragment.forbidScroll() {
  view?.viewTreeObserver?.addOnGlobalLayoutListener(object :
    ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
      view?.viewTreeObserver?.removeGlobalOnLayoutListener(this)
      BottomSheetBehavior.from(view?.parent as View).apply {
        state = BottomSheetBehavior.STATE_EXPANDED
        setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
          override fun onSlide(bottomSheet: View, slideOffset: Float) {}

          override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
              this@apply.state = BottomSheetBehavior.STATE_EXPANDED
            }
          }
        })
      }
    }
  })
}