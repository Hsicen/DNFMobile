package com.hsicen.core.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.reflect.TypeToken
import com.hsicen.core.arch.LoadState
import com.hsicen.core.data.CoroutinesDispatchers
import com.hsicen.core.data.Response
import com.hsicen.core.data.model.FileUploadResult
import com.hsicen.core.data.withIO
import com.hsicen.core.utils.gson.toObj
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import top.zibin.luban.Luban
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * 作者：hsicen  2020/8/30 13:59
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：图片上传器
 * 支持实时图片压缩以及上传进度监听.
 * 注意调用release方法释放资源
 */
interface ImageUploader {

  /** 文件上传进度. */
  val uploadProgress: LiveData<Int>

  /** 上传状态 在Loaded状态中会返回上传结果 List<FileUploadResult>. */
  val uploadState: LiveData<LoadState>

  /**
   * 上传图片.
   * @param imagePaths Array<String>
   * @param url 图片服务器
   */
  fun uploadImages(imagePaths: Array<String>, url: String, check: Boolean = false)

  /**
   * 压缩图片.
   * @param imagePaths Array<String>
   * @param url 图片服务器
   */
  fun compression(imagePaths: Array<String>, url: String, check: Boolean)

  /**
   * 释放.
   */
  fun release()

  companion object {
    operator fun invoke(ctx: Context, uploader: Uploader): ImageUploader =
      ImageUploaderImpl(ctx, uploader)
  }
}

internal class ImageUploaderImpl(
  private val ctx: Context,
  private val uploader: Uploader
) : ImageUploader, CoroutineScope {

  /** 协程Job 用于统一取消. */
  private val rootJob = Job()

  override val coroutineContext: CoroutineContext
    get() = CoroutinesDispatchers.ui + rootJob

  /** 压缩缓存，避免已压缩过的再次压缩. */
  private val compressionCache = mutableMapOf<String, File>()

  /** 压缩状态. */
  private val compressionStatus = mutableMapOf<String, Int>()

  /** 等待上传的数据. */
  private var waitUploadData: Array<String>? = null

  /** 上传进度. */
  private val _uploadProgress by lazy { MutableLiveData<Int>() }
  override val uploadProgress: LiveData<Int>
    get() = _uploadProgress

  /** 上传状态. */
  private val _uploadState by lazy { MutableLiveData<LoadState>() }
  override val uploadState: LiveData<LoadState>
    get() = _uploadState

  /**
   * 上传图片.
   * @param imagePaths Array<String>
   */
  override fun uploadImages(imagePaths: Array<String>, url: String, check: Boolean) {
    if (imagePaths.isEmpty()) return
    if (_uploadState.value == null || _uploadState.value != LoadState.Loading) {
      _uploadState.value = LoadState.Loading
    }
    if (imagePaths.all { compressionStatus[it] == COMPRESSION_COMPLETED }) {
      waitUploadData = null
      if (check) {
        doUploadImagesCheck(imagePaths.mapTo(mutableListOf()) { compressionCache[it] }, url)
      } else {
        doUploadImages(imagePaths.mapTo(mutableListOf()) { compressionCache[it] }, url)
      }
    } else {
      waitUploadData = imagePaths
      compression(imagePaths, url, check)
    }
  }

  /**
   * 执行图片上传..
   * @param uploadFiles List<File?>
   * @return Observable<List<FileUploadResult>>
   */
  private fun doUploadImages(uploadFiles: List<File?>, url: String) {
    launch {
      val files = uploadFiles.mapTo(mutableListOf()) { "files" to (it?.absolutePath ?: "") }
        .toTypedArray()
      runCatching {
        val responseBody = uploader.upload(
          "image/jpeg".toMediaTypeOrNull(),
          url,
          files,
          arrayOf(),
          onProgress = {
            _uploadProgress.postValue(it)
          }).await()
        val uploadResult: Response.List<FileUploadResult>? = responseBody.toObj(object :
          TypeToken<Response.List<FileUploadResult>>() {}.type)
        uploadResult?.unwrap() ?: throw NullPointerException("upload result is null")
      }.onSuccess {
        _uploadState.postValue(LoadState.Loaded(it.list))
      }.onFailure {
        _uploadState.postValue(LoadState.LoadError(it))
      }
    }
  }

  /**
   * 执行图片上传..
   * @param uploadFiles List<File?>
   * @return Observable<List<FileUploadResult>>
   */
  private fun doUploadImagesCheck(uploadFiles: List<File?>, url: String) {
    launch {
      val files = uploadFiles.mapTo(mutableListOf()) { "file" to (it?.absolutePath ?: "") }
        .toTypedArray()
      runCatching {
        val responseBody = uploader.upload(
          "image/jpeg".toMediaTypeOrNull(),
          url, //以后提供全局图片服务器配置
          files,
          arrayOf(),
          onProgress = {
            _uploadProgress.postValue(it)
          }).await()
        val uploadResult: Response<FileUploadResult>? = responseBody.toObj(object :
          TypeToken<Response<FileUploadResult>>() {}.type)
        uploadResult?.unwrap() ?: throw NullPointerException("upload result is null")
      }.onSuccess {
        _uploadState.postValue(LoadState.Loaded(arrayListOf(it)))
      }.onFailure {
        _uploadState.postValue(LoadState.LoadError(it))
      }
    }
  }

  /**
   * 开始压缩图片.
   * @param imagePaths Array<String>
   */
  override fun compression(imagePaths: Array<String>, url: String, check: Boolean) {
    val needCompressionImages =
      imagePaths.filter { compressionStatus[it] != COMPRESSION_STARTED && compressionStatus[it] != COMPRESSION_COMPLETED }
        .toList()
    if (needCompressionImages.isNotEmpty()) {
      needCompressionImages.forEach { compressionStatus[it] = COMPRESSION_STARTED }
      // 执行压缩操作.
      launch {
        runCatching {
          val files = doCompression(needCompressionImages)
          needCompressionImages.forEachIndexed { index, path ->
            compressionStatus[path] = COMPRESSION_COMPLETED
            compressionCache[path] = files[index]
          }
          // 如果需要上传，重新调用一次上传方法
          waitUploadData?.let { uploadImages(it, url, check) }
        }.onFailure { e ->
          Logger.e(e, "")
          needCompressionImages.forEach { compressionStatus[it] = COMPRESSION_NONE }
          if (_uploadState.value == LoadState.Loading) {
            _uploadState.value = LoadState.LoadError(e)
          }
        }
      }
    }
  }

  /**
   * 执行异步压缩.
   * @param needCompressionImages List<String>
   * @return List<File>
   */
  private suspend fun doCompression(needCompressionImages: List<String>): List<File> = withIO {
    Luban.with(ctx.applicationContext)
      .load(needCompressionImages)
      .get()
  }

  /**
   * 释放资源.
   */
  override fun release() {
    // 清空等待上传的数据.
    waitUploadData = null
    rootJob.cancel()
  }

  companion object {
    /** 没有压缩. */
    private const val COMPRESSION_NONE = 0x00

    /** 压缩开始. */
    private const val COMPRESSION_STARTED = 0X01

    /** 压缩完成. */
    private const val COMPRESSION_COMPLETED = 0X02
  }
}
