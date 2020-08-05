package com.hsicen.dnfmobile.test

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * 作者：hsicen  2020/8/3 20:45
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：DNFMobile
 */
class AppObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        //应用整个生命周期只会调用一次
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        //应用程序在前台出现时回调  后台->前台
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        //应用程序出现在前台时回调  后台->前台
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        //应用程序退出到后台时调用  前台->后台
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        //应用程序退出到后台时调用  前台->后台
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        //永远都不会调用
    }
}