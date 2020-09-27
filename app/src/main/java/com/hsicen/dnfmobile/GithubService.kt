package com.hsicen.dnfmobile

import com.hsicen.core.data.Response
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * 作者：hsicen  2020/9/27 21:20
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：DNFMobile
 */
interface GithubService {

  @GET("/users/{user}/repos")
  suspend fun listRepos(@Path("user") user: String): Response.List<Repo>
}