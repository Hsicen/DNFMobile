package com.hsicen.extensions.extensions

import android.content.Context
import android.text.SpannableStringBuilder
import android.widget.TextView
import com.hsicen.extensions.span.Span
import com.hsicen.extensions.utils.GlobalContext

/**
 * 作者：hsicen  2020/8/2 15:24
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Spannable功能扩展
 */

/**
 * 构造一个SpannableStringBuilder
 * @param ctx Context
 * @param init Span.() -> Unit
 * @return Span.Creator
 */
fun spannableStringBuilder(ctx: Context?, init: Span.() -> Unit): SpannableStringBuilder =
    Span.Creator(Span(ctx ?: GlobalContext.mContext).apply(init)).create()

/**
 * 设置一个SpannableText
 * @receiver TextView
 * @param init Span.() -> Unit
 */
fun TextView.setSpanText(init: Span.() -> Unit) {
    text = spannableStringBuilder(context, init)
}
