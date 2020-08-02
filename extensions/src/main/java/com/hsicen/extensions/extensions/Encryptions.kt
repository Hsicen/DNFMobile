package com.hsicen.extensions.extensions

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 作者：hsicen  2020/8/2 15:09
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：加密解密相关扩展
 */

/**
 * 字符串md5加密.
 * @receiver String
 * @param charset String 字符集，默认为 utf-8
 * @return String
 */
fun String.toMd5(charset: String = "utf-8"): String =
    kotlin.runCatching {
        val md = MessageDigest.getInstance("MD5")

        val byteArray = toByteArray(charset(charset))
        val md5Bytes = md.digest(byteArray)

        val hexValue = StringBuffer()
        for (i in md5Bytes.indices) {
            val value = md5Bytes[i].toInt() and 0xff
            if (value < 16) {
                hexValue.append("0")
            }
            hexValue.append(Integer.toHexString(value))
        }
        hexValue.toString()
    }.getOrNull() ?: ""

/**
 * 字符串sha256加密.
 * @receiver String
 * @return String
 */
fun String.toSha256(): String =
    kotlin.runCatching {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(this.toByteArray())
        md.digest().toHexString()
    }.getOrNull() ?: ""

/**
 * 字节数组转base64.
 * @receiver ByteArray
 * @param flag Int {@code Base64.DEFAULT}
 * @return ByteArray
 */
fun ByteArray.toBase64(flag: Int = Base64.DEFAULT): ByteArray = Base64.encode(this, flag)

/**
 *  字符串转base64.
 * @receiver String
 * @param flag Int
 * @return ByteArray
 */
fun String.toBase64(flag: Int = Base64.DEFAULT): ByteArray = Base64.encode(this.toByteArray(), flag)

/**
 * Base64解码.
 * @receiver String
 * @param flag Int {@code Base64.DEFAULT}
 * @return ByteArray
 */
fun String.base64Decode(flag: Int = Base64.DEFAULT): ByteArray = Base64.decode(this, flag)

/**
 * Base64解码.
 * @receiver ByteArray
 * @param flag Int {@code Base64.DEFAULT}
 * @return ByteArray
 */
fun ByteArray.base64Decode(flag: Int = Base64.DEFAULT): ByteArray = Base64.decode(this, flag)

/** AES算法. */
val AES_ALGORITHM by lazy { "AES/CBC/PKCS5Padding" }

/**
 * AES加密.
 * @receiver String
 * @param key String 密钥.
 * @param algorithm String 算法.
 * @return ByteArray 空则表示加密失败.
 */
fun String.aesEncrypt(key: String, algorithm: String = AES_ALGORITHM): ByteArray =
    this.toByteArray().aesEncrypt(key, algorithm)

/**
 * AES加密.
 * @receiver ByteArray
 * @param key String
 * @param algorithm String
 * @return ByteArray
 */
fun ByteArray.aesEncrypt(key: String, algorithm: String = AES_ALGORITHM): ByteArray =
    kotlin.runCatching {
        val keyBytes = key.toByteArray()
        if (keyBytes.size != 16) {
            throw RuntimeException("Invalid AES key length (must be 16 bytes)")
        }
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val enCodeFormat = secretKey.encoded
        val seckey = SecretKeySpec(enCodeFormat, "AES")
        val cipher = Cipher.getInstance(algorithm)
        val iv = IvParameterSpec(keyBytes)
        cipher.init(Cipher.ENCRYPT_MODE, seckey, iv)
        cipher.doFinal(this)
    }.onFailure { it.printStackTrace() }.getOrNull() ?: byteArrayOf()

/**
 * AES解密.
 * @receiver String
 * @param key String
 * @param algorithm String
 * @return ByteArray
 */
fun String.aesDecrypt(key: String, algorithm: String = AES_ALGORITHM): ByteArray =
    this.toByteArray().aesDecrypt(key, algorithm)

/**
 * AES解密.
 * @receiver ByteArray
 * @param key String
 * @param algorithm String
 * @return ByteArray
 */
fun ByteArray.aesDecrypt(key: String, algorithm: String = AES_ALGORITHM): ByteArray =
    kotlin.runCatching {
        if (isEmpty()) return@runCatching null
        val keyBytes = key.toByteArray()
        if (keyBytes.size != 16) {
            throw RuntimeException("Invalid AES key length (must be 16 bytes)")
        }
        val secretKey = SecretKeySpec(keyBytes, "AES")
        val enCodeFormat = secretKey.encoded
        val seckey = SecretKeySpec(enCodeFormat, "AES")
        val cipher = Cipher.getInstance(algorithm)
        val iv = IvParameterSpec(keyBytes)
        cipher.init(Cipher.DECRYPT_MODE, seckey, iv)
        cipher.doFinal(this)
    }.onFailure { it.printStackTrace() }.getOrNull() ?: byteArrayOf()