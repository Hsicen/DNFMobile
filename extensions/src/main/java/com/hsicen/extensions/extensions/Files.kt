package com.hsicen.extensions.extensions

import android.graphics.BitmapFactory
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.math.roundToInt

/**
 * 作者：hsicen  2020/8/2 15:10
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：修改文件扩展 并增加新的扩展方法
 */

const val EXTENSION_ZIP = "zip"

/**
 * 文件是否有扩展名.
 */
val File.hasExtension: Boolean
    get() = this.name.lastIndexOf(".").let { dot ->
        dot > -1 && dot < this.name.length - 1
    }

/**
 * 文件扩展名.
 */
val File.extension: String
    get() {
        if (absolutePath.isNullOrEmpty()) {
            return absolutePath
        }
        val lastPoi = absolutePath.lastIndexOf('.')
        val lastSep = absolutePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else absolutePath.substring(lastPoi + 1)
    }

/**
 * 获取不带扩展名的文件名.
 */
val File.fileNameNoEx: String?
    get() {
        val dot = this.name.lastIndexOf('.')
        if (dot > -1 && dot < this.name.length) {
            return this.name.substring(0, dot)
        }
        return name
    }

val String.hasExtension: Boolean
    get() {
        val dot = lastIndexOf('.')
        return dot > -1 && dot < length - 1
    }

/**
 * 获取文件名（去掉path）
 */
val String.filename: String
    get() {
        val sep = lastIndexOf('/')
        if (sep > -1 && sep < length - 1) {
            return substring(sep + 1)
        }
        return this
    }

/**
 * 获取扩展名
 */
val String.extension: String?
    get() {
        val dot = lastIndexOf('.')
        return if (dot > -1 && dot < length - 1) {
            substring(dot + 1)
        } else null
    }

/**
 * 获取不带扩展名的文件名
 */
val String.fileNameNoEx: String?
    get() {
        val dot = this.lastIndexOf('.')
        if (dot > -1 && dot < this.length) {
            return this.substring(0, dot)
        }
        return this
    }

/**
 * 文件大小单位.
 */
enum class SizeUnit {
    Byte,
    KB,
    MB,
    GB,
    TB,
    Auto
}

/**
 * 获取指定文件大小
 *
 * 如果是文件夹，则递归获取子文件的大小
 * @receiver File
 * @return Long
 */
fun File.getFileSize(): Long =
    when {
        !exists() -> 0L
        isFile -> inputStream().use { it.available().toLong() }
        isDirectory -> (listFiles() ?: arrayOf()).sumBy { it.getFileSize().toInt() }.toLong()
        else -> 0
    }

/**
 * long(B)转换成格式化的文件大小
 * @receiver Long
 * @param unit SizeUnit
 * @return String?
 */
fun Long.formatFileSize(unit: SizeUnit = SizeUnit.Auto): String? {
    var vUnit = unit
    if (this < 0) {
        return null
    }

    val kb = 1024.0f
    val mb = kb * 1024
    val gb = mb * 1024
    val tb = gb * 1024

    if (vUnit == SizeUnit.Auto) {
        vUnit = when {
            this < kb -> SizeUnit.Byte
            this < mb -> SizeUnit.KB
            this < gb -> SizeUnit.MB
            this < tb -> SizeUnit.GB
            else -> SizeUnit.TB
        }
    }

    return when (vUnit) {
        SizeUnit.Byte -> this.toString() + "B"
        SizeUnit.KB -> String.format(Locale.US, "%dK", (this / kb).roundToInt())
        SizeUnit.MB -> String.format(Locale.US, "%dM", (this / mb).roundToInt())
        SizeUnit.GB -> String.format(Locale.US, "%dG", this / gb)
        SizeUnit.TB -> String.format(Locale.US, "%dP", this / tb)
        else -> this.toString() + "B"
    }
}

/**
 * 将图像文件转换成base64字串
 * @receiver File
 * @return String
 */
fun File.toImageBase64(): String {
    val bytes = toByteArray()
    val encoded = Base64.encodeToString(bytes, Base64.DEFAULT)
    return "data:${getFileType()};base64,$encoded"
}

/**
 * 获取文件的图片格式
 */
fun File.getFileType(): String {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    return options.outMimeType ?: "image/jpeg"
}

/**
 * 文件转字节数组.
 * @receiver File
 * @return ByteArray
 */
fun File.toByteArray(): ByteArray = inputStream().readBytes()

/**
 * 输入流复制文件中.
 * @receiver InputStream
 * @param path String 文件路径.
 * @return File
 */
fun InputStream.copyToFile(path: String): File = copyToFile(File(path))

/**
 * 输入流复制文件中.
 * @receiver InputStream
 * @param file File
 * @return File
 */
fun InputStream.copyToFile(file: File): File {
    file.parentFile?.mkdirs()
    use { input ->
        (file.outputStream().use { output ->
            input.copyTo(output, DEFAULT_BUFFER_SIZE)
        })
    }
    return file
}

/**
 * 重写文件.
 * @receiver File
 * @param file File
 */
fun File.rewrite(file: File) = outputStream().rewrite(file)

/**
 * 输出流重写到文件中.
 * @receiver OutputStream 输出流.
 * @param file File
 */
fun OutputStream.rewrite(file: File) {
    use { output ->
        file.inputStream().use { it.copyTo(output, DEFAULT_BUFFER_SIZE) }
    }
}

/**
 * 输入流解压.
 * @receiver InputStream
 * @param path String 解压到的路径.
 */
fun InputStream.unzip(path: String) {
    val desDir = File(path)
    if (!desDir.exists()) {
        desDir.mkdirs()
    }
    // 先保存成文件再解压
    val temp = File("${desDir.parentFile}/temp.dat")
    this.copyToFile(temp).unzip(path)
    // 操作完成后删文件
    temp.delete()
}

/**
 * 文件解压.
 * @receiver File
 * @param path String 解压到的路径.
 */
fun File.unzip(path: String) {
    val desDir = File(path)
    if (!desDir.exists()) {
        desDir.mkdirs()
    }
    val zipFile = ZipFile(this)
    val entries = zipFile.entries()
    while (entries.hasMoreElements()) {
        val entry = entries.nextElement() as ZipEntry
        val desFile = File(path + File.separator + entry.name)
        if (entry.isDirectory) {
            desFile.mkdirs()
            continue
        }
        if (!desFile.exists()) {
            val fileParentDir = desFile.parentFile
            if (fileParentDir?.exists() != true) {
                fileParentDir?.mkdirs()
            }
            desFile.createNewFile()
        }
        val ins = zipFile.getInputStream(entry)
        val outs = desFile.outputStream()
        ins.copyTo(outs, 1024 * 1024)
        ins.close()
        outs.close()
    }
    zipFile.close()
}

/**
 *  读取文件中的json串
 *  @param path 路径
 *  @return 返回的json串
 */
fun File.getJsonFromFile(): String? {

    var fis: FileInputStream? = null
    var fileLock: FileLock? = null
    var channel: FileChannel? = null
    if (this.exists()) {

        try {
            fis = FileInputStream(this)
            channel = fis.channel
            fileLock = channel.tryLock(0, Long.MAX_VALUE, true)
            return fis.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            com.orhanobut.logger.Logger.e("extensions.Files", e.toString())
        } finally {
            if (fileLock?.isValid == true) {
                fileLock.release()
            }
            if (channel?.isOpen == true) {
                channel.close()
            }
            fis?.close()
        }
    }
    return null
}