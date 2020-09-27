package com.hsicen.dnfmobile

import com.hsicen.core.data.safeApiCall
import javax.inject.Inject

/**
 * 作者：hsicen  2020/9/27 21:30
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：DNFMobile
 */
class GithubPresenter @Inject constructor(private val mGithubService: GithubService) {

  suspend fun fetchUserRepo(user: String) = safeApiCall {
    mGithubService.listRepos(user)
  }

}