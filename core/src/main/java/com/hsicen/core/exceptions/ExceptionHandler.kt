package com.hsicen.core.exceptions

import com.bumptech.glide.load.HttpException
import com.hsicen.core.R
import com.hsicen.core.data.Response
import com.hsicen.core.data.ServerCodes
import com.hsicen.core.utils.toast.KToast.info
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.utils.GlobalContext
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CancellationException
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 * 作者：hsicen  2020/8/10 22:46
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：全局异常处理
 */

/**
 * 网络请求异常处理.
 * @receiver Exception
 * @param clazz
 * @param default ((Exception) -> Boolean)?
 */
fun Throwable?.handleForNetwork(
  clazz: Class<*>? = null,
  default: ((Throwable) -> Boolean)? = null
) {
  when (this) {
    is Response.Exception -> {
      // 会话过期不提示
      (this.response.code == ServerCodes.TOKEN_EXPIRED).no {
        info(this.message ?: "")
      }
    }
    is SocketException,
    is SocketTimeoutException,
    is HttpException -> {
      info(GlobalContext.getContext().getString(R.string.error_not_network))
    }

    is CancellationException -> {

    }

    else -> {
      if (default?.invoke(this ?: return) != true) {
        info(GlobalContext.getContext().getString(R.string.error_not_network))
      }
    }
  }
  handle(clazz)
}

/**
 * 异常处理.
 * @receiver Throwable
 * @param clazz
 */
fun Throwable?.handle(clazz: Class<*>? = null) {
  if (clazz == null) {
    Logger.e(this, "${this?.message}")
  } else {
    Logger.e(this, "${this?.message}  -->  ${clazz.name}")
  }
}
