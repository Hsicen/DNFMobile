package com.hsicen.dnfmobile.test

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * 作者：hsicen  2020/8/3 20:20
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：定位类
 * locationChange 位置变化回调函数
 */
class MyLocator(private val locationChange: (Double, Double) -> Unit) : LifecycleObserver {

    init {
        initLocator()
    }

    private fun initLocator() {
        //相关初始化操作
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun startLocate() {
        //开始定位相关逻辑
        locationChange.invoke(123.3, 34.7) //位置变化回调
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun stopLocate() {
        //停止定位
        locationChange.invoke(33.6, 31.9) //位置变化回调
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun destroyLocator() {
        //资源销毁
    }
}