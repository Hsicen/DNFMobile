@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.hsicen.extensions.extensions

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * 作者：hsicen  2020/8/2 15:22
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：资源功能扩展
 */

inline fun Context.dimen(@DimenRes resId: Int) = resources.getDimensionPixelSize(resId)
inline fun Fragment.dimen(@DimenRes resId: Int) = context?.dimen(resId) ?: 0
inline fun View.dimen(@DimenRes resId: Int) = context.dimen(resId)

inline fun Context.color(@ColorRes resId: Int) = ContextCompat.getColor(this, resId)
inline fun Fragment.color(@ColorRes resId: Int) = context?.color(resId) ?: 0
inline fun View.color(@ColorRes resId: Int) = context.color(resId)

inline fun Context.drawableRes(@DrawableRes resId: Int) = ContextCompat.getDrawable(this, resId)
inline fun Fragment.drawableRes(@DrawableRes resId: Int) = context?.drawableRes(resId)
inline fun View.drawableRes(@DrawableRes resId: Int) = context.drawableRes(resId)