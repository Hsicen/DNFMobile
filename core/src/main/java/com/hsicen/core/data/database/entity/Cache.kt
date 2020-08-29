package com.hsicen.core.data.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 作者：hsicen  2020/8/29 22:42
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：key-value 缓存表.
 *
 * @property key 唯一key值.
 * @property value 内容.
 * @property userId 关联的用户id.
 * @property timestamp 缓存的时间.
 */
@Keep
@Entity(tableName = "cache")
data class Cache(
  @PrimaryKey
  val key: String,
  val value: String,
  @ColumnInfo(name = "user_id")
  var userId: Int?,
  var timestamp: Long = System.currentTimeMillis()
)