package com.hsicen.core.ui.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

/**
 * 分界线.
 * Created by tomlezen.
 * Data: 2019/4/22.
 * Time: 17:13.
 * @property size Int 分界线大小
 * @property color Int? 分界线颜色 为空即表示透明.
 * @property paddingStart Int 左内间距.
 * @property paddingEnd Int 右内间距.
 * @property startOffsetCount Int 开始偏移item个数.
 * @property endOffsetCount Int 结束偏移item个数.
 * @param topOrBottom 分割线在上方还是下方开始.
 */
class KItemDecoration(
  @Px private val size: Int,
  @ColorInt private val color: Int? = null,
  @Px private val paddingStart: Int = 0,
  @Px private val paddingEnd: Int = 0,
  private val startOffsetCount: Int = 0,
  private val endOffsetCount: Int = 0,
  @RecyclerView.Orientation private val orientation: Int = RecyclerView.VERTICAL
) : RecyclerView.ItemDecoration() {

  private val colorDrawable = color?.let { ColorDrawable(it) }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    colorDrawable?.let {
      val itemCount = parent.adapter?.itemCount ?: 0
      (0 until parent.childCount)
        .map { parent.getChildAt(it) }
        .forEach { child ->
          val pos = parent.getChildAdapterPosition(child)
          if (startOffsetCount <= pos && (itemCount - endOffsetCount) > pos) {
            colorDrawable.setBounds(
              paddingStart,
              child.bottom,
              parent.right - paddingEnd,
              child.bottom + size
            )
            colorDrawable.draw(c)
          }
        }
    }
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val position = parent.getChildAdapterPosition(view)
    val itemCount = parent.adapter?.itemCount ?: 0
    if (startOffsetCount <= position && (itemCount - endOffsetCount) > position) {
      if (orientation == RecyclerView.VERTICAL) {
        outRect.set(0, 0, 0, size)
      } else {
        outRect.set(0, 0, size, 0)
      }
    }
  }
}