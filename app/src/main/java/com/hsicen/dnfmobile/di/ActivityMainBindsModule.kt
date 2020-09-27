package com.hsicen.dnfmobile.di

import androidx.lifecycle.ViewModel
import com.hsicen.core.dagger.module.ViewModelAssistedFactory
import com.hsicen.core.dagger.module.ViewModelKey
import com.hsicen.dnfmobile.GithubViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * 作者：hsicen  2020/9/27 21:57
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：DNFMobile
 */

@Module
abstract class ActivityMainBindsModule {

  @Binds
  @IntoMap
  @ViewModelKey(GithubViewModel::class)
  abstract fun bindFactory(factory: GithubViewModel.Factory): ViewModelAssistedFactory<out ViewModel>
}