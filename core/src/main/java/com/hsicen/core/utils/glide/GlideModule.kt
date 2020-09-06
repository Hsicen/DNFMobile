package com.hsicen.core.utils.glide

import android.app.ActivityManager
import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * 作者：hsicen  2020/9/6 15:52
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Glide配置模块
 */
@GlideModule
class GlideModule : AppGlideModule() {

  override fun applyOptions(context: Context, builder: GlideBuilder) {
    val reqOpts = RequestOptions()
      .format(if ((context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).isLowRamDevice) DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888)
      .disallowHardwareConfig()
    builder.setDefaultRequestOptions(reqOpts)
  }

  override fun isManifestParsingEnabled(): Boolean = false
}