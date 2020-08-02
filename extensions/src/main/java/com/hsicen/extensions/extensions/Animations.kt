package com.hsicen.extensions.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


/**
 * view 属性动画
 */
fun View.getObjectAnimator(
    durations: Long = 500L,
    propertyName: String = "alpha",
    values: FloatArray = floatArrayOf(1f, 0f),
    interrogators: Interpolator? = null,
    animationEnd: ((Boolean) -> Unit)? = null
): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, propertyName, *values).apply {
        duration = durations
        if (interrogators != null) {
            interpolator = interrogators
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animationEnd?.invoke(true)
            }
        })
    }
}

/**
 * view 补间动画
 */
fun View.loadAnimation(
    @AnimRes ids: Int, durations: Long = 250L,
    interrogators: Interpolator? = FastOutSlowInInterpolator(),
    animationEnd: ((Boolean) -> Unit)? = null
): Animation {
    return AnimationUtils.loadAnimation(context, ids).apply {
        if (interrogators != null) {
            interpolator = interrogators
        }
        duration = durations
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                animationEnd?.invoke(true)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }
}

/**
 * view 补间动画
 */
fun Context.loadAnimation(
    @AnimRes ids: Int, durations: Long = 250L,
    interrogators: Interpolator? = FastOutSlowInInterpolator(),
    animationEnd: ((Boolean) -> Unit)? = null
): Animation {
    return AnimationUtils.loadAnimation(this, ids).apply {
        if (interrogators != null) {
            interpolator = interrogators
        }
        duration = durations
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                animationEnd?.invoke(true)
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }
}

/**
 * 动态缩小时间文本控件的字体大小
 * @param textSize
 * @param delay
 */
fun TextView?.updateDateTextSize(
    textSize: Float,
    lineCount: Int = 2,
    delay: Long = 10,
    callBack: ((status: Boolean) -> Unit)? = null
) {
    this?.postDelayed({
        if (this.lineCount > lineCount) {
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            this.updateDateTextSize(textSize - 0.5f, lineCount, delay, callBack)
        } else {
            callBack?.invoke(true)
        }
    }, delay)
}

