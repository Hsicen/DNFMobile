package com.hsicen.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hsicen.core.data.database.converter.Converters
import com.hsicen.core.data.database.dao.CacheDao
import com.hsicen.core.data.database.entity.Cache

/**
 * 作者：hsicen  2020/8/29 15:03
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Room数据库
 */
@Database(
  entities = [
    Cache::class
  ],
  version = 1,
  exportSchema = true
)
@TypeConverters(Converters::class)
abstract class CoreDatabase : RoomDatabase() {

  /**
   * 缓存dao.
   * @return CacheDao
   */
  abstract fun cacheDao(): CacheDao
}
