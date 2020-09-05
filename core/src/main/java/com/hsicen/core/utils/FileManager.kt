package com.hsicen.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import com.hsicen.core.data.withIO
import com.hsicen.extensions.extensions.getFileSize
import com.hsicen.extensions.extensions.getFileType
import com.hsicen.extensions.extensions.save2File
import java.io.File

/**
 * 作者：hsicen  2020/8/30 14:26
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：文件管理器
 */
class FileManager(private val context: Context) {

  /** 图片缓存路径. */
  private val _imageCachePath by lazy { File(context.cacheDir, "image").absolutePath }
  val imageCachePath: String
    get() {
      _imageCachePath.checkFileExist()
      return _imageCachePath
    }

  /** 相册路径. */
  private val _dcimPath by lazy {
    File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
      "mDnf"
    ).absolutePath
  }
  val dcimPath: String
    get() {
      _dcimPath.checkFileExist()
      return _dcimPath
    }

  /** 下载缓存路径. */
  private val _downloadCachePath by lazy { File(context.externalCacheDir, "mDnf").absolutePath }
  val downloadCachePath: String
    get() {
      _downloadCachePath.checkFileExist()
      return _downloadCachePath
    }

  /**
   * 计算缓存大小.
   * @return Long
   */
  suspend fun calculateCacheSize(): Long = withIO {
    context.cacheDir.getFileSize() + (context.externalCacheDir?.getFileSize() ?: 0L)
  }

  /**
   * 清空缓存.
   * @return Boolean
   */
  suspend fun clearCache() = withIO {
    context.cacheDir.deleteRecursively()
    context.externalCacheDir?.deleteRecursively()
  }

  /**
   * 保存到相册.
   * @param bitmap Bitmap
   * @param fileName String
   */
  fun saveToDICM(bitmap: Bitmap?, fileName: String): String {
    bitmap ?: return ""
    val path = "$dcimPath/$fileName"
    val file = bitmap.save2File(path) ?: throw Exception("save file failed")
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val uri = Uri.fromFile(file)
    intent.data = uri
    context.sendBroadcast(intent)
    MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf(file.getFileType())) { _, _ -> }
    return path
  }

  /**
   * 复制文件到相册.
   * @param file File?
   * @param fileName String
   * @return Boolean
   */
  fun copyToDICM(context: Context, file: File?, fileName: String): Boolean {
    file ?: return false
    return runCatching {
      val path = "$dcimPath/$fileName"
      val file1 = file.copyTo(File(path))
      val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
      val uri = Uri.fromFile(file1)
      intent.data = uri
      context.sendBroadcast(intent)
      MediaScannerConnection.scanFile(context, arrayOf(file1.absolutePath), arrayOf(file1.getFileType())) { _, _ ->
      }
      true
    }.getOrNull() ?: false
  }

  /**
   * 检查文件是否存在.
   * @receiver String
   */
  private fun String.checkFileExist() {
    File(this).apply {
      if (!exists()) {
        mkdirs()
        createNewFile()
      }
    }
  }
}
