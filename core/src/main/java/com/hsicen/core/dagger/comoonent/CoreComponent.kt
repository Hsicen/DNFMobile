package com.hsicen.core.dagger.comoonent

import com.hsicen.core.CoreApplication
import com.hsicen.core.dagger.module.CoreDataModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * 作者：hsicen  2020/8/26 23:30
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：核心组件 (主编织图)
 */

@Singleton
@Component(
  modules = [
    AndroidInjectionModule::class,
    CoreDataModule::class
  ]
)
interface CoreComponent : AndroidInjector<CoreApplication>, CoreComponentProvider {

  @Component.Builder
  interface Builder {

    @BindsInstance
    fun seedInstance(instance: CoreApplication): Builder

    fun coreDataModule(module: CoreDataModule): Builder

    fun build(): CoreComponent
  }
}