package com.hsicen.dnfmobile.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.hsicen.core.dagger.module.ViewModelAssistedFactory
import com.hsicen.core.dagger.module.ViewModelFactory
import com.hsicen.core.net.ApiServices
import com.hsicen.core.net.create
import com.hsicen.dnfmobile.GithubService
import com.hsicen.dnfmobile.GithubViewModel
import com.hsicen.dnfmobile.MainActivity
import dagger.Module
import dagger.Provides

/**
 * 作者：hsicen  2020/9/27 22:05
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：提供外部依賴
 */

@Module
class ActivityMainModule {

  @Provides
  fun provideViewModel(
    viewModelMap: MutableMap<Class<out ViewModel>, ViewModelAssistedFactory<out ViewModel>>,
    activity: MainActivity
  ): GithubViewModel =
    ViewModelProviders.of(
      activity,
      ViewModelFactory(viewModelMap, activity, activity.intent.extras)
    ).get(GithubViewModel::class.java)

  @Provides
  fun provideGithubService(retrofit: ApiServices): GithubService = retrofit.create()

}