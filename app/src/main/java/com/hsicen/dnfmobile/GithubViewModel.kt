package com.hsicen.dnfmobile

import androidx.lifecycle.SavedStateHandle
import com.hsicen.core.arch.CoreViewModel
import com.hsicen.core.dagger.module.ViewModelAssistedFactory
import com.hsicen.core.data.onFailure
import com.hsicen.core.data.onSuccess
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 作者：hsicen  2020/9/27 21:51
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：DNFMobile
 */
class GithubViewModel @Inject constructor(private val githubPresenter: GithubPresenter) : CoreViewModel() {

  fun fetchUserRepo(user: String) = launch {
    githubPresenter.fetchUserRepo(user)
      .onSuccess {
        Logger.d("hsc", "获取用户信息成功")
      }.onFailure {
        Logger.d("hsc", "获取用户信息失败")
      }
  }

  class Factory @Inject constructor(
    private val githubPresenter: GithubPresenter
  ) : ViewModelAssistedFactory<GithubViewModel> {
    override fun create(handle: SavedStateHandle): GithubViewModel =
      GithubViewModel(githubPresenter)
  }
}