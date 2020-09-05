package com.hsicen.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hsicen.core.data.model.Cache

/**
 * 作者：hsicen  2020/8/29 22:42
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：缓存Dao
 */
@Dao
interface CacheDao {

  /**
   * 插入缓存.
   * @param data Cache
   * @return Long
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(data: Cache): Long

  /**
   * 插入多条缓存.
   * @param data List<Cache>
   * @return Long
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(data: List<Cache>)

  /**
   * 根据key删除缓存.
   * @param key String 缓存key值.
   * @return Deferred<Int>
   */
  @Query("DELETE FROM cache WHERE `key` = :key")
  suspend fun delete(key: String): Int

  /**
   * 删除多个key缓存.
   * @param keys List<String>
   * @return Int
   */
  @Query("DELETE FROM cache WHERE `key` in (:keys)")
  suspend fun delete(keys: List<String>): Int

  /**
   * 根据key获取缓存.
   * @param key String
   * @return Deferred<List<Cache>>
   */
  @Query("SELECT * FROM cache WHERE `key`=:key")
  suspend fun query(key: String): Cache

  /**
   * 根据key和user_id获取缓存.
   * @param key String
   * @param useId Int
   * @return Cache
   */
  @Query("SELECT * FROM cache WHERE `key`=:key AND user_id=:useId")
  suspend fun query(key: String, useId: Int): Cache

  /**
   * 获取多个key缓存.
   * @param keys List<String>
   * @return List<Cache>
   */
  @Query("SELECT * FROM cache WHERE `key` in (:keys)")
  suspend fun query(keys: List<String>): List<Cache>

  /**
   * 清空.
   * @return List<Cache>
   */
  @Query("DELETE FROM cache")
  suspend fun clear()
}
