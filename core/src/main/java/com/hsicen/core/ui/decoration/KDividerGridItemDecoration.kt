package com.hsicen.core.ui.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 作者：hsicen  2020/9/6 16:25
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Grid分割线
 */
class KDividerGridItemDecoration(private val divider: Drawable) : RecyclerView.ItemDecoration() {

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    drawHorizontal(c, parent)
    drawVertical(c, parent)
  }

  private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val params = child.layoutParams as RecyclerView.LayoutParams
      val left = child.left - params.leftMargin
      val right = (child.right + params.rightMargin + divider.intrinsicWidth)
      val top = child.bottom + params.bottomMargin
      val bottom = top + divider.intrinsicHeight
      divider.setBounds(left, top, right, bottom)
      divider.draw(c)
    }
  }

  private fun drawVertical(c: Canvas, parent: RecyclerView) {
    val childCount = parent.childCount
    for (i in 0 until childCount) {
      val child = parent.getChildAt(i)
      val params = child.layoutParams as RecyclerView.LayoutParams
      val top = child.top - params.topMargin
      val bottom = child.bottom + params.bottomMargin
      val left = child.right + params.rightMargin
      val right = left + divider.intrinsicWidth

      divider.setBounds(left, top, right, bottom)
      divider.draw(c)
    }
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    outRect.set(0, 0, divider.intrinsicWidth, 0)
  }
}