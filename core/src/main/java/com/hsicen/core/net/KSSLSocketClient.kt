package com.hsicen.core.net

import android.annotation.SuppressLint
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * 作者：hsicen  2020/9/5 18:10
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：https配置
 */
object KSSLSocketClient {

  val sslSocketFactory: SSLSocketFactory by lazy {
    runCatching {
      val sslContext = SSLContext.getInstance("SSL")
      sslContext.init(null, arrayOf<TrustManager>(x509TrustManager), SecureRandom())
      sslContext.socketFactory
    }.getOrThrow()
  }

  val x509TrustManager: X509TrustManager by lazy {
    object : X509TrustManager {
      override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()

      @SuppressLint("TrustAllX509TrustManager")
      override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
      }

      @SuppressLint("TrustAllX509TrustManager")
      override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
      }
    }
  }

  val hostnameVerifier: HostnameVerifier by lazy { HostnameVerifier { _, _ -> true } }
}