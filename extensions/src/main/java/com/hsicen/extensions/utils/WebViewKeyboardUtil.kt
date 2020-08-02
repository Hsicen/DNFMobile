package com.hsicen.extensions.utils

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout

/**
 * 作者：hsicen  2020/8/2 11:37
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：兼容webView 软键盘问题
 *
 * 直接在弹出软键盘的页面调用：WebViewKeyboardUtil(this)
 */
class WebViewKeyboardUtil constructor(activity: Activity) {

    private val mChildOfContent: View
    private var usableHeightPrevious: Int = 0
    private val frameLayoutParams: FrameLayout.LayoutParams
    private var contentHeight: Int = 0
    private var isFirst = true
    private val statusBarHeight: Int

    init {
        /**
         * 获取状态栏的高度
         */
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = activity.resources.getDimensionPixelSize(resourceId)
        val content = activity.findViewById<View>(android.R.id.content) as FrameLayout
        mChildOfContent = content.getChildAt(0)
        /**
         * 界面出现变动都会调用这个监听事件
         */
        mChildOfContent.viewTreeObserver.addOnGlobalLayoutListener {
            if (isFirst) {
                contentHeight = mChildOfContent.height//兼容华为等机型
                isFirst = false
            }
            possiblyResizeChildOfContent()
        }
        frameLayoutParams = mChildOfContent.layoutParams as FrameLayout.LayoutParams
    }

    /**
     * 重新调整跟布局的高度
     */
    private fun possiblyResizeChildOfContent() {
        val usableHeightNow = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard = mChildOfContent.rootView.height
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            if (heightDifference > usableHeightSansKeyboard / 4) {
                frameLayoutParams.height =
                    usableHeightSansKeyboard - heightDifference + statusBarHeight
            } else {
                frameLayoutParams.height = contentHeight
            }
            mChildOfContent.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    /**
     * 计算mChildOfContent可见高度
     * @return
     */
    private fun computeUsableHeight(): Int {
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top
    }

}
