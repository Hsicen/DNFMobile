package com.hsicen.extensions.extensions

/**
 * 作者：hsicen  2020/8/2 15:01
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Collections扩展
 */

/**
 *  将一根list分组
 *  @param n 要分成几组
 *  @return 分组后的数据
 */
fun <T> Collection<T?>.groupByNum(n: Int): List<List<T?>?>? {

    if (this is List<T?>) {

        if (this.size == 1) {
            return arrayListOf(this)
        }
        val yushu = this.size % n
        // 求分组数
        val count0 = this.size / n + (if (yushu != 0) 1 else 0)

        val data = ArrayList<List<T?>>()

        for (i in 0 until count0) {
            val temp = arrayListOf<T?>()
            var end = (i + 1) * n
            if (end > this.size) {
                end = this.size
            }
            temp.addAll(this.subList(i * n, end))
            data.add(temp)
        }
        return data
    } else {
        return null
    }
}

/**
 * Returns this List if it's not `null` and the empty list otherwise.
 */
fun <T> ArrayList<T>?.orEmpty(): ArrayList<T> = this ?: ArrayList()