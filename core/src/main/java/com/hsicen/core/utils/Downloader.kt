package com.hsicen.core.utils

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okio.*
import java.io.File
import java.io.FileNotFoundException

/**
 * 作者：hsicen  2020/8/29 23:12
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：文件下载器
 */
interface KDownloader {

  /**
   * 下载.
   * @param downloadUrl String 下载地址.
   * @param saveFileDir String 保存文件夹.
   * @return Deferred<File>
   */
  fun download(downloadUrl: String, saveFileDir: String): Deferred<File> =
    download(downloadUrl, saveFileDir, downloadUrl.fileName(), null)

  /**
   * 下载.
   * @param downloadUrl String 下载地址.
   * @param saveFileDir String 保存文件夹.
   * @param saveFileName String 保存文件名.
   * @return Deferred<File>
   */
  fun download(downloadUrl: String, saveFileDir: String, saveFileName: String): Deferred<File> =
    download(downloadUrl, saveFileDir, saveFileName, null)

  /**
   * 下载.
   * @param downloadUrl String 下载地址.
   * @param saveFileDir String 保存文件夹.
   * @param onProgress (totalSize: Long, progress: Float) -> Unit 下载进度.
   * @return Deferred<File>
   */
  fun download(downloadUrl: String, saveFileDir: String, onProgress: (totalSize: Long, progress: Float) -> Unit): Deferred<File> =
    download(downloadUrl, saveFileDir, downloadUrl.fileName(), onProgress)

  /**
   * 下载.
   * @param downloadUrl String 下载地址.
   * @param saveFileDir String 保存文件夹.
   * @param saveFileName String 保存文件名.
   * @param onProgress ((totalSize: Long, progress: Float) -> Unit)? 下载进度.
   * @return Deferred<File>
   */
  fun download(downloadUrl: String, saveFileDir: String, saveFileName: String, onProgress: ((totalSize: Long, progress: Float) -> Unit)? = null): Deferred<File>

  companion object {
    operator fun invoke(client: OkHttpClient): KDownloader = KDownloaderImpl(client)
  }
}

internal class KDownloaderImpl constructor(private val client: OkHttpClient) : KDownloader {

  override fun download(downloadUrl: String, saveFileDir: String, saveFileName: String, onProgress: ((totalSize: Long, progress: Float) -> Unit)?): Deferred<File> {
    val deferred = CompletableDeferred<File>()
    val request = Request.Builder()
      .url(downloadUrl)
      .tag(downloadUrl)
      .addHeader("Accept-Encoding", "identity")
      .build()
    // val newClient = client.newBuilder()
    //     .addNetworkInterceptor {
    //         val originalResponse = it.proceed(it.request())
    //         originalResponse.newBuilder()
    //             .body(ProgressResponseBody(originalResponse.body, onProgress))
    //             .build()
    //     }
    //     .build()
    val call = client.newCall(request)
    deferred.invokeOnCompletion {
      if (deferred.isCancelled) {
        call.cancel()
      }
    }

    val response = call.execute()
    response.body?.let { body ->
      runCatching {
        if (deferred.isCancelled) {
          call.cancel()
        } else {
          body.byteStream().use {
            val saveDir = File(saveFileDir).apply {
              if (!exists()) {
                mkdirs()
              }
            }
            val fileName = "$saveFileName.temp"
            val file = File(saveDir, fileName)
            // it.copyToFile(file)
            // val saveFile = File(saveFileDir, saveFileName)
            // if (file.renameTo(saveFile)) {
            //     if (!deferred.isCancelled) {
            //         deferred.complete(saveFile)
            //     }
            // } else if (!deferred.isCancelled) {
            //     deferred.completeExceptionally(FileNotFoundException())
            // }
            val totalSize = body.contentLength()
            file.outputStream().use { out ->
              var bytesCopied: Long = 0
              val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
              var bytes = it.read(buffer)
              while (bytes >= 0 && !deferred.isCancelled) {
                out.write(buffer, 0, bytes)
                bytesCopied += bytes
                onProgress?.invoke(totalSize, bytesCopied * 1.0f / totalSize * 100)
                bytes = it.read(buffer)
              }
              val saveFile = File(saveFileDir, saveFileName)
              if (file.renameTo(saveFile)) {
                if (!deferred.isCancelled) {
                  deferred.complete(saveFile)
                }
              } else if (!deferred.isCancelled) {
                deferred.completeExceptionally(FileNotFoundException())
              }
            }
          }
        }
      }.onFailure {
        deferred.completeExceptionally(it)
      }.getOrNull()
    } ?: deferred.completeExceptionally(NullPointerException("response body is null"))
    return deferred
  }
}

internal class ProgressResponseBody(
  private val responseBody: ResponseBody?,
  private val onProgress: ((totalSize: Long, progress: Float) -> Unit)? = null
) : ResponseBody() {

  override fun contentLength(): Long = responseBody?.contentLength() ?: -1

  override fun contentType(): MediaType? = responseBody?.contentType()

  override fun source(): BufferedSource {
    return source(responseBody!!.source()).buffer()
  }

  private fun source(source: Source): Source {
    return object : ForwardingSource(source) {
      var totalBytesRead = 0L

      override fun read(sink: Buffer, byteCount: Long): Long {
        val bytesRead = super.read(sink, byteCount)

        totalBytesRead += if (bytesRead != -1L) bytesRead else 0L
        onProgress?.invoke(contentLength(), totalBytesRead * 1f / contentLength() * 100)
        return bytesRead
      }
    }
  }
}

/**
 * 根据url获取文件名.
 * @receiver String
 * @return String
 */
internal fun String.fileName(): String {
  val separatorIndex = lastIndexOf("/")
  var name = if (separatorIndex < 0) this else substring(separatorIndex + 1, length)
  name = name.replace("/", "_").replace("?", "_").replace("-", "_")
  // 避免文件名过长
  if (name.length > 30) {
    name = name.substring(0, 30)
  }
  return "$name.temp"
}