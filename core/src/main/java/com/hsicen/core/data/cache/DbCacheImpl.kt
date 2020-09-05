package com.hsicen.core.data.cache

import com.hsicen.core.data.database.dao.CacheDao
import com.hsicen.core.data.model.Cache
import com.orhanobut.logger.Logger

/**
 * 作者：hsicen  2020/8/29 23:10
 * 邮箱：codinghuang@163.com
 * 作用：
 * 描述：缓存数据库实现类
 */
class DbCacheImpl internal constructor(private val cacheDao: CacheDao) : DbCache {

  override suspend fun set(key: String, value: String, userId: Int?): Boolean =
    set(Cache(key, value, userId))

  override suspend fun set(cache: Cache): Boolean =
    (runCatching {
      cacheDao.insert(cache)
    }.onFailure {
      Logger.e(it, "set db cache failed: cache = $cache")
    }.getOrNull() ?: 0) > 0

  override suspend fun set(caches: List<Cache>): Boolean =
    runCatching {
      cacheDao.insert(caches)
      true
    }.onFailure {
      Logger.e(it, "get db cache failed: caches = $caches")
    }.getOrNull() ?: false

  override suspend fun get(key: String, userId: Int): Cache? =
    runCatching {
      cacheDao.query(key, userId)
    }.onFailure {
      Logger.e(it, "get db cache failed: key = $key")
    }.getOrNull()

  override suspend fun get(key: String): Cache? =
    runCatching {
      cacheDao.query(key)
    }.onFailure {
      Logger.e(it, "get db cache failed: key = $key")
    }.getOrNull()

  override suspend fun get(vararg key: String): List<Cache> =
    runCatching {
      cacheDao.query(key.toList())
    }.onFailure {
      Logger.e(it, "get db cache failed: keys = $key")
    }.getOrNull() ?: listOf()

  override suspend fun remove(vararg key: String): Int =
    removeKeys(key.toList())

  override suspend fun removeKeys(keys: List<String>): Int =
    runCatching {
      cacheDao.delete(keys)
    }.onFailure {
      Logger.e(it, "remove db cache failed: keys = $keys")
    }.getOrNull() ?: -1

  override suspend fun clear() {
    runCatching {
      cacheDao.clear()
    }
  }
}
