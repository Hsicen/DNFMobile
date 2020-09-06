package com.hsicen.core.utils.glide

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes

import com.hsicen.core.R
import com.hsicen.extensions.extensions.color
import com.hsicen.extensions.extensions.dimen
import com.hsicen.extensions.extensions.drawableRes
import com.hsicen.extensions.utils.GlobalContext
import kotlin.math.max
import kotlin.math.min

/**
 * 作者：hsicen  2020/9/6 15:54
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：全局默认图片drawable
 *
 * @property backgroundColorRes Int 背景颜色.
 * @property backgroundRadius Int 背景圆角.
 * @property centerDrawableResId Int 背景颜色.
 */
class DefImageDrawable(
  @ColorRes val backgroundColorRes: Int = R.color.bg_1,
  @DimenRes val backgroundRadiusDimenRes: Int = 0,
  @DrawableRes val centerDrawableResId: Int = R.drawable.ic_empty_image,
  @DimenRes val leftMarginDimenRes: Int = 0,
  @DimenRes val rightMarginDimenRes: Int = 0,
  @DimenRes val topMarginDimenRes: Int = 0,
  @DimenRes val bottomMarginDimenRes: Int = 0
) : Drawable(), Parcelable {

  private val ctx by lazy { GlobalContext.getContext() }

  private val backgroundColor by lazy { ctx.color(backgroundColorRes) }
  private val backgroundRadius by lazy {
    if (backgroundRadiusDimenRes == 0) 0 else ctx.dimen(
      backgroundRadiusDimenRes
    )
  }
  private val leftMargin by lazy {
    if (leftMarginDimenRes == 0) 0 else ctx.dimen(
      leftMarginDimenRes
    )
  }
  private val rightMargin by lazy {
    if (rightMarginDimenRes == 0) 0 else ctx.dimen(
      rightMarginDimenRes
    )
  }
  private val topMargin by lazy {
    if (topMarginDimenRes == 0) 0 else ctx.dimen(
      topMarginDimenRes
    )
  }
  private val bottomMargin by lazy {
    if (bottomMarginDimenRes == 0) 0 else ctx.dimen(
      bottomMarginDimenRes
    )
  }
  private val centerDrawable by lazy { ctx.drawableRes(centerDrawableResId) }

  private val backgroundPaint by lazy {
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = backgroundColor
    }
  }

  constructor(parcel: Parcel) : this(
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt(),
    parcel.readInt()
  )

  init {
    // 计算最小的大小
    setBounds(0, 0, centerDrawable?.minimumWidth ?: 0, centerDrawable?.minimumHeight ?: 0)
  }

  override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
    super.setBounds(left, top, right, bottom)
    val centerDrawableW = centerDrawable?.intrinsicWidth ?: 0
    val centerDrawableH = centerDrawable?.intrinsicHeight ?: 0
    if (bounds.width() * 2 / 3 < centerDrawableW || bounds.height() * 2 / 3 < centerDrawableH) {
      centerDrawable?.setBounds(
        max(bounds.centerX() - centerDrawableW / 3, bounds.left),
        max(bounds.centerY() - centerDrawableH / 3, bounds.top),
        min(bounds.centerX() + centerDrawableW / 3, bounds.right),
        min(bounds.centerY() + centerDrawableH / 3, bounds.bottom)
      )
    } else {
      centerDrawable?.setBounds(
        max(bounds.centerX() - centerDrawableW / 2, bounds.left),
        max(bounds.centerY() - centerDrawableH / 2, bounds.top),
        min(bounds.centerX() + centerDrawableW / 2, bounds.right),
        min(bounds.centerY() + centerDrawableH / 2, bounds.bottom)
      )
    }
  }

  override fun draw(canvas: Canvas) {
    if (!bounds.isEmpty) {
      // 先绘制背景
      canvas.drawRoundRect(
        bounds.left.toFloat() + leftMargin,
        bounds.top.toFloat() + topMargin,
        bounds.right.toFloat() - rightMargin,
        bounds.bottom.toFloat() - bottomMargin,
        backgroundRadius.toFloat(),
        backgroundRadius.toFloat(),
        backgroundPaint
      )

      centerDrawable?.draw(canvas)
    }
  }

  override fun setAlpha(alpha: Int) {
    // do nothing
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {
    // do nothing
  }

  override fun getOpacity(): Int = PixelFormat.OPAQUE
  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeInt(backgroundColorRes)
    parcel.writeInt(backgroundRadiusDimenRes)
    parcel.writeInt(centerDrawableResId)
    parcel.writeInt(leftMarginDimenRes)
    parcel.writeInt(rightMarginDimenRes)
    parcel.writeInt(topMarginDimenRes)
    parcel.writeInt(bottomMarginDimenRes)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<DefImageDrawable> {
    override fun createFromParcel(parcel: Parcel): DefImageDrawable {
      return DefImageDrawable(parcel)
    }

    override fun newArray(size: Int): Array<DefImageDrawable?> {
      return arrayOfNulls(size)
    }
  }
}
