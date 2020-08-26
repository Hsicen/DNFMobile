package com.hsicen.core

import androidx.multidex.MultiDexApplication
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/**
 * 作者：hsicen  2020/8/22 12:07
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：Application 核心配置
 *
 * 支持MultiDex.
 */
open class CoreApplication : MultiDexApplication(), HasAndroidInjector {

    @Inject
    lateinit var dispatchAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {

        return dispatchAndroidInjector
    }


}