@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.hsicen.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import com.hsicen.core.BuildConfig
import com.hsicen.core.R
import com.hsicen.core.data.ui
import com.hsicen.extensions.extensions.inflate
import com.hsicen.extensions.utils.GlobalContext
import kotlinx.android.synthetic.main.k_toast_default.view.*
import java.lang.reflect.Proxy


/**
 * 作者：hsicen  2020/8/10 22:48
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Toast封装， 目前只封装一部分.
 */
object KToast {

    /** 全局Context. */
    private val context by lazy { GlobalContext.getContext() }

    /** 最新显示的Toast实例. */
    private var toast: Toast? = null

    /**
     * 记录显示的内容
     */
    private var message: String = ""

    /**
     * handler
     */
    private val handler: Handler = Handler(Looper.getMainLooper())

    /**
     * runnable回调
     */
    private val runnable = Runnable {
        message = ""
    }

    /**
     * 弹出Toast.
     * @param message String? 信息.
     * @param duration Int Toast.LENGTH_SHORT
     */
    fun info(message: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (message.isNullOrEmpty() || message == this.message) return
        ui {
            this@KToast.message = message
            this@KToast.handler.postDelayed(runnable, 1000)
            (toast?.apply {
                this.duration = duration
                view?.tv_toast_message?.text = message
            } ?: Toast(context).apply {
                this.view = context.inflate(R.layout.toast_default)
                this.duration = duration
                view?.tv_toast_message?.text = message
                this@KToast.toast = this
            })
            if (isNotificationEnable(context)) {
                toast?.show()
            } else {
                showSystemToast(toast)
            }
        }
    }

    fun error(message: String?, duration: Int = Toast.LENGTH_SHORT) {
        info(message, duration)
    }

    fun success(message: String?, duration: Int = Toast.LENGTH_SHORT) {
        info(message, duration)
    }

    /**兼容部分手机toast不兼容问题
     * @param toast
     */
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi", "SoonBlockedPrivateApi")
    fun showSystemToast(toast: Toast?) {
        runCatching {
            val serviceMethod = Toast::class.java.getDeclaredMethod("getService")
            serviceMethod.isAccessible = true
            val invoke = serviceMethod.invoke(toast)
            val forName = Class.forName("android.app.INotificationManager")
            //设置代理，将toast里面的方法参数替换掉,将应用包名替换为系统包名进行校验
            val notificationManagerProxy = Proxy.newProxyInstance(
                toast?.javaClass?.classLoader,
                arrayOf(forName)
            ) { _, method, args ->
                //强制使用系统Toast。华为p20 pro上为enqueueToastEx
                if ("enqueueToast" == method.name || "enqueueToastEx" == method.name) {
                    args[0] = "android"
                }
                method.invoke(invoke, *args)
            }
            val serviceFiled = Toast::class.java.getDeclaredField("sService")
            serviceFiled.isAccessible = true
            serviceFiled.set(toast, notificationManagerProxy)
            toast?.show()
        }
    }
}

inline fun debugInfo(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    if (BuildConfig.DEBUG) {
        KToast.info(message, duration)
    }
}

inline fun info(message: String?, duration: Int = Toast.LENGTH_SHORT) =
    KToast.info(message, duration)

inline fun info(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    KToast.info(GlobalContext.getContext().getString(resId), duration)

inline fun error(message: String?, duration: Int = Toast.LENGTH_SHORT) =
    KToast.info(message, duration)

inline fun error(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    KToast.info(GlobalContext.getContext().getString(resId), duration)

inline fun success(message: String?, duration: Int = Toast.LENGTH_SHORT) =
    KToast.info(message, duration)

inline fun success(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    KToast.info(GlobalContext.getContext().getString(resId), duration)


/** 检测消息通知是否开启
 * @param context
 */
fun isNotificationEnable(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}
