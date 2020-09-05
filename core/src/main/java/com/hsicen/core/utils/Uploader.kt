package com.hsicen.core.utils

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.*
import java.io.File
import java.io.IOException
import java.net.URLConnection

/**
 * 作者：hsicen  2020/8/30 13:45
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：上传器
 */
interface Uploader {

  /**
   * 上传单个文件.
   * @param serviceUrl String
   * @param fileKey String
   * @param fileValue String
   * @return Deferred<String>
   */
  fun upload(type: MediaType? = null, serviceUrl: String, fileKey: String, fileValue: String) =
    upload(type, serviceUrl, arrayOf(Pair(fileKey, fileValue)), null, null)

  /**
   * 上传单个文件.
   * @param serviceUrl String
   * @param fileKey String
   * @param fileValue String
   * @param params Array<Pair<String, String>>
   * @return Deferred<String>
   */
  fun upload(
    type: MediaType? = null,
    serviceUrl: String,
    fileKey: String,
    fileValue: String,
    params: Array<Pair<String, String>>
  ) =
    upload(type, serviceUrl, arrayOf(Pair(fileKey, fileValue)), params, null)

  /**
   * 上传单个文件.
   * @param serviceUrl String
   * @param fileKey String
   * @param fileValue String
   * @param params Array<Pair<String, String>>
   * @param onProgress (progress: Int) -> Unit
   * @return Deferred<String>
   */
  fun upload(
    type: MediaType? = null,
    serviceUrl: String,
    fileKey: String,
    fileValue: String,
    params: Array<Pair<String, String>>,
    onProgress: (progress: Int) -> Unit
  ) =
    upload(type, serviceUrl, arrayOf(Pair(fileKey, fileValue)), params, onProgress)

  /**
   * 上传多个文件.
   * @param serviceUrl String
   * @param files Array<Pair<String, String>>
   * @param params Array<Pair<String, String>>?
   * @param onProgress ((progress: Int) -> Unit)?
   * @return Deferred<String>
   */
  fun upload(
    type: MediaType? = null,
    serviceUrl: String,
    files: Array<Pair<String, String>>,
    params: Array<Pair<String, String>>?,
    onProgress: ((progress: Int) -> Unit)? = null
  ): Deferred<String>

  companion object {
    operator fun invoke(okHttpClient: OkHttpClient): Uploader = UploaderImpl(okHttpClient)
  }
}

internal class UploaderImpl(private val okHttpClient: OkHttpClient) : Uploader {

  override fun upload(
    type: MediaType?,
    serviceUrl: String,
    files: Array<Pair<String, String>>,
    params: Array<Pair<String, String>>?,
    onProgress: ((progress: Int) -> Unit)?
  ): Deferred<String> {
    val deferred = CompletableDeferred<String>()
    val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
    val headersBuilder = Headers.Builder()
    params?.forEach {
      headersBuilder.add(it.first, it.second)
    }
    val headers = headersBuilder.build()
    if (headers.size > 0) {
      builder.addPart(headers, "".toRequestBody(null))
    }
    files.filter { File(it.second).exists() }
      .forEach {
        val file = File(it.second)
        val fileName = file.name
        builder.addFormDataPart(
          it.first,
          fileName,
          file.asRequestBody(type ?: fileName.guessMimeType().toMediaTypeOrNull())
        )
      }
    try {
      val requestBody = builder.build()
      val request = Request.Builder()
        .url(serviceUrl)
        .post(if (onProgress == null) requestBody else ProgressRequestBody(requestBody, onProgress))
        .tag(serviceUrl)
        .build()
      val call = okHttpClient.newCall(request)
      call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          deferred.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
          if (!deferred.isCancelled) {
            if (response.isSuccessful) {
              val content =
                response.body?.string() ?: throw NullPointerException("response body is null")
              deferred.complete(content)
            } else {
              deferred.completeExceptionally(Exception("code = ${response.code}"))
            }
          }
        }
      })
    } catch (e: Exception) {
      deferred.completeExceptionally(e)
    }
    return deferred
  }

  fun String.guessMimeType(): String =
    URLConnection.getFileNameMap().getContentTypeFor(this) ?: "application/octet-stream"
}

/**
 * 进度RequestBody.
 * @property requestBody RequestBody
 * @property onProgress Function1<[@kotlin.ParameterName] Int, Unit>
 * @constructor
 */
internal class ProgressRequestBody(
  private val requestBody: RequestBody,
  private val onProgress: ((progress: Int) -> Unit)? = null
) : RequestBody() {

  override fun contentType(): MediaType? = requestBody.contentType()

  override fun contentLength(): Long = runCatching {
    requestBody.contentLength()
  }.getOrNull() ?: -1

  override fun writeTo(sink: BufferedSink) {
    val bufferedSink = sink(sink).buffer()
    requestBody.writeTo(bufferedSink)
    bufferedSink.flush()
  }

  private fun sink(sink: Sink) = object : ForwardingSink(sink) {
    var bytesWritten = 0L
    var contentLength = 0L

    override fun write(source: Buffer, byteCount: Long) {
      super.write(source, byteCount)
      if (contentLength == 0L) {
        contentLength = contentLength()
      }
      bytesWritten += byteCount
      onProgress?.invoke((bytesWritten / contentLength * 100f).toInt())
    }
  }
}