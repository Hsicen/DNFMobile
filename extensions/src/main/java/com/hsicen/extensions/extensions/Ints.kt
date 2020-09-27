package com.hsicen.extensions.extensions

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import com.hsicen.extensions.utils.GlobalContext

/**
 * 作者：hsicen  2020/8/2 15:12
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Int扩展
 */

fun <T : Drawable> Int.mutateDrawable(): T? =
  kotlin.runCatching {
    Resources.getSystem().getDrawable(this).mutate() as? T
  }.getOrNull()

fun Int.mutateColorStateList(): ColorStateList? =
  kotlin.runCatching {
    val resources = Resources.getSystem()
    val xml = resources.getXml(this)
    ColorStateList.createFromXml(resources, xml)
  }.getOrNull()

fun Int.layoutIdToBitmap(width: Int, height: Int): Bitmap {
  val view = LayoutInflater.from(GlobalContext.mContext).inflate(this, null)
  val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
  val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
  view.measure(widthSpec, heightSpec)
  view.layout(0, 0, width, height)
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  view.draw(canvas)
  return bitmap
}