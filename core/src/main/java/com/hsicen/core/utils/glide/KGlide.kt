package com.hsicen.core.utils.glide

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hsicen.core.R
import com.hsicen.extensions.extensions.dp2px
import com.hsicen.extensions.extensions.no
import com.hsicen.extensions.extensions.yes
import java.security.MessageDigest

/**
 * 作者：hsicen  2020/9/6 16:06
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Glide图片加载封装
 */

val defImageDrawable by lazy { DefImageDrawable() }
val defImageDrawableRadius2dp by lazy { DefImageDrawable(backgroundRadiusDimenRes = R.dimen.def_image_drawable_radius_2_dp) }
val defImageDrawableRadius4dp by lazy { DefImageDrawable(backgroundRadiusDimenRes = R.dimen.def_image_drawable_radius_4_dp) }
val defImageDrawableRadius6dp by lazy { DefImageDrawable(backgroundRadiusDimenRes = R.dimen.def_image_drawable_radius_6_dp) }
val defImageDrawableRadius6dpAndMargin by lazy {
  DefImageDrawable(
    backgroundRadiusDimenRes = R.dimen.def_image_drawable_radius_6_dp,
    leftMarginDimenRes = R.dimen.def_margin_left,
    rightMarginDimenRes = R.dimen.def_margin_left,
    topMarginDimenRes = R.dimen.def_margin_top,
    bottomMarginDimenRes = R.dimen.def_margin_top
  )
}

/** 默认请求选项. */
private val defRequestOptionsOrigin by lazy {
  RequestOptions()
    .error(defImageDrawable)
    .placeholder(defImageDrawable)
}

/** 默认请求选项. */
private val defRequestOptions by lazy {
  RequestOptions()
    .centerCrop()
    .error(defImageDrawable)
    .placeholder(defImageDrawable)
}

/** 默认用户头像请求选项. */
val defUserHeadRequestOptions by lazy {
  RequestOptions()
    .circleCrop()
    .error(R.drawable.ic_user_head_default)
    .placeholder(R.drawable.ic_user_head_default)
}

fun Context.glide() = Glide.with(this)
fun Fragment.glide() = Glide.with(this)
fun View.glide() = Glide.with(this)

/**
 * 加载图片.
 * @receiver ImageView
 * @param url String?
 * @param options (RequestOptions.() -> Unit)?
 */
fun ImageView.loadImage(
  url: String?,
  errorCallBack: (() -> Unit)? = null,
  resourceReadyCallback: ((r: Drawable?) -> Unit)? = null,
  options: (RequestOptions.() -> Unit)? = null
) {
  val ops = RequestOptions().apply(defRequestOptions).apply {
    options?.invoke(this)
  }
  url.isNullOrEmpty().yes {
    // 是空的话 直接显示错误图片.
    if (ops.errorPlaceholder != null) {
      this.setImageDrawable(ops.errorPlaceholder)
    } else {
      this.setImageResource(ops.errorId)
    }
  }.no {
    runCatching {
      glide().load(url)
        .apply(ops)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
          ): Boolean {
            errorCallBack?.invoke()
            return false
          }

          override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            resourceReadyCallback?.invoke(resource)
            return false
          }
        })
        .into(this)
    }.onFailure {
      if (ops.errorPlaceholder != null) {
        this.setImageDrawable(ops.errorPlaceholder)
      } else {
        this.setImageResource(ops.errorId)
      }
    }
  }
}

/**
 * 加载图片.
 * @receiver ImageView
 * @param url String?
 * @param options (RequestOptions.() -> Unit)?
 */
fun ImageView.loadImageOrigin(
  url: String?,
  errorCallBack: (() -> Unit)? = null,
  resourceReadyCallback: ((r: Drawable?) -> Unit)? = null,
  options: (RequestOptions.() -> Unit)? = null
) {
  val ops = RequestOptions().apply(defRequestOptionsOrigin).apply {
    options?.invoke(this)
  }
  url.isNullOrEmpty().yes {
    // 是空的话 直接显示错误图片.
    if (ops.errorPlaceholder != null) {
      this.setImageDrawable(ops.errorPlaceholder)
    } else {
      this.setImageResource(ops.errorId)
    }
  }.no {
    runCatching {
      glide().load(url)
        .apply(ops)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
          ): Boolean {
            errorCallBack?.invoke()
            return false
          }

          override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            resourceReadyCallback?.invoke(resource)
            return false
          }
        })
        .into(this)
    }.onFailure {
      if (ops.errorPlaceholder != null) {
        this.setImageDrawable(ops.errorPlaceholder)
      } else {
        this.setImageResource(ops.errorId)
      }
    }
  }
}

/**
 * 加载圆角图片.
 * @receiver ImageView
 * @param url String?
 * @param radius Int? 圆角半径默认为10
 * @param options (RequestOptions.() -> Unit)?
 */
fun ImageView.loadRoundImage(
  url: String?,
  radius: Int = context.dp2px(4),
  options: (RequestOptions.() -> Unit)? = null
) {
  val ops = RequestOptions().apply(defRequestOptions).apply {
    options?.invoke(this)
  }
  url.isNullOrEmpty().yes {
    // 是空的话 直接显示错误图片.
    if (ops.errorPlaceholder != null) {
      this.setImageDrawable(ops.errorPlaceholder)
    } else {
      this.setImageResource(ops.errorId)
    }
  }.no {
    runCatching {
      glide().load(url).apply(ops.transforms(CenterCrop(), RoundedCorners(radius))).into(this)
    }.onFailure {
      if (ops.errorPlaceholder != null) {
        this.setImageDrawable(ops.errorPlaceholder)
      } else {
        this.setImageResource(ops.errorId)
      }
    }
  }
}

/**
 * 加载用户头像.
 * @receiver ImageView
 * @param url String?
 * @param options (RequestOptions.() -> Unit)?
 */
fun ImageView.loadUserHead(url: String?, options: (RequestOptions.() -> Unit)? = null) =
  loadImage(url) {
    apply(defUserHeadRequestOptions).apply {
      options?.invoke(this)
    }
  }

/**
 * 加载选项.
 * @receiver RequestBuilder<Drawable>
 * @param options RequestOptions.() -> Unit
 * @return RequestBuilder<(android.graphics.androidDrawable.Drawable..android.graphics.androidDrawable.Drawable?)>
 */
fun RequestBuilder<Drawable>.options(options: RequestOptions.() -> Unit) =
  apply(RequestOptions().also { options.invoke(it) })

/**
 * 圆角.
 * @receiver RequestOptions
 * @param radius Float?
 * @return RequestOptions
 */
fun RequestOptions.round(radius: Float?) =
  this.also { transform(GlideRoundTransform(radius ?: 0f)) }

/**
 * 圆角变换.
 * @property radius Float
 * @constructor
 */
class GlideRoundTransform(private val radius: Float) : BitmapTransformation() {

  override fun updateDiskCacheKey(messageDigest: MessageDigest) {
  }

  override fun transform(
    pool: BitmapPool,
    toTransform: Bitmap,
    outWidth: Int,
    outHeight: Int
  ): Bitmap? =
    roundCrop(pool, TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight))

  /**
   * 裁剪圆角.
   * @param pool BitmapPool
   * @param source Bitmap?
   * @return Bitmap?
   */
  private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
    if (source == null) return null

    var result: Bitmap? = pool.get(source.width, source.height, Bitmap.Config.ARGB_8888)
    if (result == null) {
      result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(result!!)
    val paint = Paint()
    paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.isAntiAlias = true
    val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
    canvas.drawRoundRect(rectF, radius, radius, paint)
    return result
  }
}
