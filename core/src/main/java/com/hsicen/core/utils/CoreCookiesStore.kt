package com.hsicen.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import com.hsicen.extensions.extensions.hexStringToByteArray
import com.hsicen.extensions.extensions.toHexString
import com.orhanobut.logger.Logger
import java.io.*
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

/**
 * 作者：hsicen  2020/9/1 22:34
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Cookie存储
 */
class CoreCookiesStore(private val context: Context) : CookieStore {

  private val cookiePrefs by lazy { context.getSharedPreferences(COOKIE_PREFS_NAME, Context.MODE_PRIVATE) }

  /** 当前所有cookie */
  private val cookies = mutableMapOf<String, ConcurrentHashMap<String, HttpCookie>>()

  init {
    readCookieToMemory()
  }

  /**
   * 读取cookie到内存.
   */
  private fun readCookieToMemory() {
    val prefsMap = cookiePrefs.all
    for ((key, value) in prefsMap) {
      if (value is String && !value.startsWith(COOKIE_NAME_PREFIX)) {
        val cookieNames = value.split(", ")
        for (name in cookieNames) {
          cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null)?.let {
            decodeCookie(it)?.let { cookie ->
              if (!cookies.containsKey(key))
                cookies[key] = ConcurrentHashMap()
              cookies[key]?.set(name, cookie)
            }
          }
        }
      }
    }
  }

  private fun getCookieToken(uri: URI?, cookie: HttpCookie?): String = cookie?.name ?: "" + cookie?.domain

  override fun removeAll(): Boolean {
    edit {
      clear()
    }
    cookies.clear()
    return true
  }

  override fun add(uri: URI?, cookie: HttpCookie?) {
    cookie?.let {
      val token = getCookieToken(uri, cookie)
      val key = uri?.host ?: return
      if (!cookie.hasExpired()) {
        if (!cookies.containsKey(key)) {
          cookies[key] = ConcurrentHashMap()
        }
        cookies[key]?.put(token, cookie)
      } else {
        if (cookies.containsKey(key)) {
          cookies[key]?.remove(token)
        }
      }
      Logger.e("$cookie")
      edit {
        putString(key, cookies[key]?.keys?.joinToString { it })
        putString(COOKIE_NAME_PREFIX + token, encodeCookie(SerializableHttpCookie(cookie)))
      }
    }
  }

  override fun getCookies(): MutableList<HttpCookie> = mutableListOf<HttpCookie>().apply {
    cookies.values.forEach { this.addAll(it.values) }
  }

  override fun getURIs(): MutableList<URI> = cookies.keys.mapTo(mutableListOf()) { URI(it) }

  override fun remove(uri: URI?, cookie: HttpCookie?): Boolean =
    kotlin.runCatching {
      val name = getCookieToken(uri, cookie)
      if (cookies.containsKey(uri?.host) && cookies[uri?.host]?.containsKey(name) == true) {
        cookies[uri?.host ?: return@runCatching false]?.remove(name)

        edit {
          if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
            remove(COOKIE_NAME_PREFIX + name)
          }
          putString(uri.host, cookies[cookie?.domain ?: return@edit]?.keys?.joinToString { it })
        }
        return@runCatching true
      }
      return@runCatching false
    }.getOrNull() ?: false

  override fun get(uri: URI?): MutableList<HttpCookie> = mutableListOf<HttpCookie>().apply {
    if (cookies.containsKey(uri?.host ?: return@apply)) {
      this.addAll(cookies[uri.host]?.values ?: listOf())
    }
  }

  /**
   * Cookie编码.
   * @param cookie SerializableHttpCookie?
   * @return String?
   */
  private fun encodeCookie(cookie: SerializableHttpCookie?): String =
    cookie?.let {
      runCatching {
        ByteArrayOutputStream().apply {
          ObjectOutputStream(this).writeObject(cookie)
        }
      }.onFailure {
        Logger.e(it, "")
      }.getOrNull()?.toByteArray()?.toHexString()
    } ?: ""

  /**
   * Cookie解码.
   * @param cookieString String
   * @return HttpCookie?
   */
  private fun decodeCookie(cookieString: String): HttpCookie? =
    runCatching {
      val bytes = cookieString.hexStringToByteArray()
      val objectInputStream = ObjectInputStream(ByteArrayInputStream(bytes))
      (objectInputStream.readObject() as SerializableHttpCookie).getCookie()
    }.onFailure {
      Logger.e(it, "")
    }.getOrNull()

  /**
   * 编辑.
   * @param action SharedPreferences.Editor.() -> Unit
   */
  private fun edit(action: SharedPreferences.Editor.() -> Unit) {
    cookiePrefs.edit().apply(action).apply()
  }

  companion object {
    /** Cookie sp缓存. */
    private const val COOKIE_PREFS_NAME = "cookie_sp_cache"

    /** Cookie前缀. */
    private const val COOKIE_NAME_PREFIX = "cookie_"
  }
}

@Keep
class SerializableHttpCookie(private var cookie: HttpCookie?) : Serializable {

  @Transient
  private var clientCookie: HttpCookie? = null

  fun getCookie(): HttpCookie? {
    if (clientCookie != null) {
      cookie = clientCookie
    }
    return cookie
  }

  private fun writeObject(out: ObjectOutputStream) {
    out.writeObject(cookie?.name)
    out.writeObject(cookie?.value)
    out.writeObject(cookie?.comment)
    out.writeObject(cookie?.commentURL)
    out.writeObject(cookie?.domain)
    out.writeLong(cookie?.maxAge ?: 0L)
    out.writeObject(cookie?.path)
    out.writeObject(cookie?.portlist)
    out.writeBoolean(cookie?.secure ?: false)
    out.writeBoolean(cookie?.discard ?: false)
    out.writeInt(cookie?.version ?: 0)
  }

  private fun readObject(inputStream: ObjectInputStream) {
    val name = inputStream.readObject() as String
    val value = inputStream.readObject() as String
    clientCookie = HttpCookie(name, value)
    clientCookie?.comment = inputStream.readObject() as? String
    clientCookie?.commentURL = inputStream.readObject() as? String
    clientCookie?.domain = inputStream.readObject() as? String
    clientCookie?.maxAge = inputStream.readLong()
    clientCookie?.path = inputStream.readObject() as? String
    clientCookie?.portlist = inputStream.readObject() as? String
    clientCookie?.secure = inputStream.readBoolean()
    clientCookie?.discard = inputStream.readBoolean()
    clientCookie?.version = inputStream.readInt()
  }
}
