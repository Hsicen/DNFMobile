package com.hsicen.core.utils

import android.content.Context
import com.hsicen.core.GlobalConfigs
import com.hsicen.core.R
import com.hsicen.core.ui.dialog.showKAlert
import com.hsicen.extensions.extensions.makeDial

/**
 * 作者：hsicen  2020/9/6 17:23
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：全局统一的拨打电话
 */
object KCalls {

  /*** 拨打电话提示.*/
  fun contactService(ctx: Context?, phoneNum: String = GlobalConfigs.CONSUMER_PHONE) {
    ctx?.showKAlert {
      message = phoneNum
      negativeText = ctx.getString(R.string.action_cancel)
      positiveText = ctx.getString(R.string.action_call)
    }?.onPositiveButtonClick {
      it.dismiss()
      ctx.makeDial(phoneNum)
    }
  }

  /*** 直接拨号*/
  fun dialService(context: Context?, phoneNumber: String = GlobalConfigs.CONSUMER_PHONE) {
    context?.makeDial(phoneNumber)
  }
}
