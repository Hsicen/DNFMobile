package com.hsicen.dnfmobile.di

import com.hsicen.core.dagger.comoonent.CoreComponent
import com.hsicen.core.dagger.scope.ModuleScope
import com.hsicen.dnfmobile.MainActivity
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

/**
 * 作者：hsicen  2020/9/27 21:57
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：DNFMobile
 */

@ModuleScope
@Component(
  modules = [
    AndroidInjectionModule::class,
    ActivityMainModule::class,
    ActivityMainBindsModule::class
  ],
  dependencies = [CoreComponent::class]
)
interface ActivityMainComponent : AndroidInjector<MainActivity> {

  @Component.Builder
  abstract class Builder : AndroidInjector.Builder<MainActivity>() {
    /**
     * 引入和核心组件.
     * @param coreComponent CoreComponent
     * @return Builder
     */
    abstract fun coreComponent(coreComponent: CoreComponent): Builder
  }
}