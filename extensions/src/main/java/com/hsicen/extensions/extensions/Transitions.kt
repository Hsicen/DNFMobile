package com.hsicen.extensions.extensions

import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionManager

/**
 * 作者：hsicen  2020/8/2 15:25
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Transition动画扩展
 */

/**
 * 延迟执行Transition动画.
 * @receiver ViewGroup
 * @param transition Transition?
 */
fun ViewGroup.beginDelayedTransition(transition: Transition? = null) {
    TransitionManager.beginDelayedTransition(this, transition)
}

/**
 * 动画结束后执行.
 * @receiver Transition
 * @param onEnd () -> Unit
 */
inline fun Transition.doOnEnd(crossinline onEnd: () -> Unit) {
    addListener(object : Transition.TransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            onEnd.invoke()
        }

        override fun onTransitionResume(transition: Transition) {
        }

        override fun onTransitionPause(transition: Transition) {
        }

        override fun onTransitionCancel(transition: Transition) {
        }

        override fun onTransitionStart(transition: Transition) {
        }
    })
}