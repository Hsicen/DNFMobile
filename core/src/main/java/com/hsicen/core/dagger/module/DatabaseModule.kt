package com.hsicen.core.dagger.module

import android.content.Context
import androidx.room.Room
import com.hsicen.core.data.cache.DbCache
import com.hsicen.core.data.database.CoreDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * 作者：hsicen  2020/9/5 18:15
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：数据库模块
 */
@Module
class DatabaseModule {

  /**
   * 提供Room数据库实例.
   * @param ctx Context
   * @return CoreDatabase
   */
  @Provides
  @Singleton
  fun provideDatabase(ctx: Context): CoreDatabase =
    Room.databaseBuilder(ctx, CoreDatabase::class.java, DATA_BASE_NAME)
      //addMigrations  数据库升级
      .build()

  /**
   * 提供数据缓存实例.
   * @param database CoreDatabase
   * @return DbCache
   */
  @Singleton
  @Provides
  fun provideDbCache(database: CoreDatabase): DbCache =
    DbCache(database.cacheDao())

  companion object {
    /** 数据库名. */
    private const val DATA_BASE_NAME = "hsicen"
  }
}
