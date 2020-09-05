package com.hsicen.core.dagger.module

import android.content.Context
import com.google.gson.Gson
import com.hsicen.core.BuildConfig
import com.hsicen.core.GlobalConfigs
import com.hsicen.core.arouter.ARouters
import com.hsicen.core.data.converter.GsonConverterCompatFactory
import com.hsicen.core.net.ApiServices
import com.hsicen.core.net.ApiServicesImpl
import com.hsicen.core.net.HttpHeadersInterceptor
import com.hsicen.core.net.KSSLSocketClient
import com.hsicen.core.utils.*
import dagger.Module
import dagger.Provides
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 作者：hsicen  2020/9/1 22:30
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：网络相关模块
 */
@Module
class NetModule {

  /** 区别与接口请求okhttp，用户文件下载等. */
  private val toolOkHttp: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .retryOnConnectionFailure(false)
      .callTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .readTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .writeTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .connectTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .build()
  }

  /**
   * 提供CoreCookiesStore.
   * @param context Context
   * @return CoreCookiesStore
   */
  @Singleton
  @Provides
  fun provideCookiesStore(context: Context): CookieStore = CoreCookiesStore(context)

  /**
   * 提供OkHttp实例.
   * @param context Context
   * @param interceptor HttpLoggingInterceptor
   * @param cookiesStore CookieStore
   * @return OkHttpClient
   */
  @Provides
  fun provideOkHttpClient(
    context: Context,
    interceptor: HttpLoggingInterceptor,
    cookiesStore: CookieStore,
    kImei: KImei
  ): OkHttpClient =
    OkHttpClient.Builder()
      .cookieJar(JavaNetCookieJar(CookieManager(cookiesStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER)))
      .addInterceptor(HttpHeadersInterceptor(context, kImei))
      .addInterceptor(interceptor)
      .callTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .readTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .writeTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .connectTimeout(GlobalConfigs.HTTP_TIME_OUT, TimeUnit.SECONDS)
      .retryOnConnectionFailure(false)
      .sslSocketFactory(KSSLSocketClient.sslSocketFactory, KSSLSocketClient.x509TrustManager)
      .hostnameVerifier(KSSLSocketClient.hostnameVerifier)
      .build()

  /**
   * 提供Http日志拦截器实例.
   * @return HttpLoggingInterceptor
   */
  @Provides
  fun provideLoggingInterceptor(): HttpLoggingInterceptor =
    HttpLoggingInterceptor().apply {
      level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
      } else {
        HttpLoggingInterceptor.Level.NONE
      }
    }

  /**
   * 提供GsonConverterFactory实例.
   * @param gson Gson
   * @return GsonConverterCompatFactory
   */
  @Provides
  fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
    GsonConverterFactory.create(gson)

  /**
   * 提供Retrofit实例.
   * @param okHttpClient OkHttpClient
   * @param gson Gson
   * @return Retrofit
   */
  @Singleton
  @Provides
  fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gson: Gson
  ): Retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(ARouters.Main.getHttpServerHost())
    .addConverterFactory(GsonConverterCompatFactory(gson))
    .build()

  @Singleton
  @Provides
  fun provideKApiServices(
    retrofit: Retrofit
  ): ApiServices = ApiServicesImpl(retrofit)

  /**
   * 下载器.
   * @return KDownloader
   */
  @Singleton
  @Provides
  fun provideDownloader(): Downloader = Downloader(toolOkHttp)

  /**
   * 上传器.
   * @param okHttpClient OkHttpClient
   * @return KUploader
   */
  @Singleton
  @Provides
  fun provideUploader(okHttpClient: OkHttpClient): Uploader = Uploader(okHttpClient)

  /**
   * 图片上传器.
   * @param context Context
   * @param uploader KUploader
   * @return KImageUploader
   */
  @Provides
  fun provideImageUploader(context: Context, uploader: Uploader) = ImageUploader(context, uploader)
}
