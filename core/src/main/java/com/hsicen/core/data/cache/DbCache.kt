package com.hsicen.core.data.cache

import com.hsicen.core.data.database.dao.CacheDao
import com.hsicen.core.data.database.entity.Cache

/**
 * 作者：hsicen  2020/8/29 23:09
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：数据库缓存
 */
interface DbCache {
  /**
   * 设置缓存.
   * @param key String 缓存key.
   * @param value String 缓存值.
   * @param userId Int? 用户id（可选参数）
   * @return Boolean 是否成功.
   */
  suspend fun set(key: String, value: String, userId: Int? = null): Boolean

  /**
   * 设置缓存.
   * @param cache Cache 缓存实体.
   * @return Boolean 是否成功.
   */
  suspend fun set(cache: Cache): Boolean

  /**
   * 设置多个缓存.
   * @param caches List<Cache>
   * @return Boolean
   */
  suspend fun set(caches: List<Cache>): Boolean

  /**
   * 获取缓存.
   * @param key String 缓存key.
   * @return Cache? 缓存实体（有可能未取到缓存内容）.
   */
  suspend fun get(key: String): Cache?

  /**
   * 获取缓存.
   * @param key String 缓存key.
   * @param userId Int 用户id.
   * @return Cache?
   */
  suspend fun get(key: String, userId: Int): Cache?

  /**
   * 获取缓存.
   * @param key Array<out String>
   * @return Cache?
   */
  suspend fun get(vararg key: String): List<Cache>

  /**
   * 删除缓存.
   * @param key Array<out String>
   * @return Int 成功删除缓存的条数.
   */
  suspend fun remove(vararg key: String): Int

  /**
   * 删除缓存.
   * @param keys List<String>
   * @return Int 成功删除缓存的条数.
   */
  suspend fun removeKeys(keys: List<String>): Int

  /**
   * 清空.
   */
  suspend fun clear()

  companion object {
    /**
     * 创建实例.
     * @param cacheDao CacheDao
     * @return DbCache
     */
    operator fun invoke(cacheDao: CacheDao): DbCache = DbCacheImpl(cacheDao)
  }
}
