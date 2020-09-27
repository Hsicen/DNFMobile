package com.hsicen.dnfmobile

import com.alibaba.android.arouter.facade.annotation.Route
import com.hsicen.core.arch.InjectedActivity
import com.hsicen.core.arouter.ARouters
import com.hsicen.core.coreComponent
import com.hsicen.dnfmobile.di.DaggerActivityMainComponent
import com.hsicen.extensions.extensions.clickThrottle
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@Route(path = ARouters.Main.MAIN)
class MainActivity : InjectedActivity(R.layout.activity_main) {

  @Inject
  lateinit var mGithubViewModel: GithubViewModel

  override fun inject() {
    DaggerActivityMainComponent.builder()
      .coreComponent(coreComponent())
      .create(this)
      .inject(this)
  }

  override fun initView() {
    super.initView()

    tvFetchRepo.clickThrottle {

      mGithubViewModel.fetchUserRepo("hsicen")
    }
  }


}